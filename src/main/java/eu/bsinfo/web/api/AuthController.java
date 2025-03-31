package eu.bsinfo.web.api;

import eu.bsinfo.db.StatementBuilder;
import eu.bsinfo.db.enums.Tables;
import eu.bsinfo.utils.SHAUtils;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.LoginDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.crypto.SecretKey;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Logger;

@Path("/auth")
public class AuthController {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode("q5XJkV0lA9n5eG6yQ/+LdGQ6T+dqz5S/fkQItwpo8xE="));
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    @GET
    public Response isAuthorized(@HeaderParam("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = validateToken(token);
        if (username == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            ResultSet rs = new StatementBuilder()
                    .select(new String[]{"session"})
                    .from(Tables.USERS)
                    .where("username", "=", username)
                    .executeQuery(Server.getDbConn());

            if (!rs.next()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            String jwt = rs.getString("session");
            if (jwt == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDto loginDto) {
        try {
            ResultSet rs = new StatementBuilder()
                    .select(new String[]{"salt", "password"})
                    .from(Tables.USERS)
                    .where("username", "=", loginDto.getUsername())
                    .executeQuery(Server.getDbConn());

            if (!rs.next()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            String salt = rs.getString("salt");
            String password = rs.getString("password");

            if (SHAUtils.getSHA512(salt, loginDto.getPassword()).equals(password)) {
                String token = generateToken(loginDto.getUsername());

                new StatementBuilder()
                        .update(Tables.USERS, new String[]{"session"}, token)
                        .where("username", "=", loginDto.getUsername())
                        .executeUpdate(Server.getDbConn());

                return Response.ok(Collections.singletonMap("token", token)).build();
            }

            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    public Response logout(@HeaderParam("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = validateToken(token);
        if (username == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            new StatementBuilder()
                    .update(Tables.USERS, new String[]{"session"}, (Object) null)
                    .where("username", "=", username)
                    .executeUpdate(Server.getDbConn());

            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY).build()
                    .parseSignedClaims(token.replace("Bearer ", ""))
                    .getPayload();
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}

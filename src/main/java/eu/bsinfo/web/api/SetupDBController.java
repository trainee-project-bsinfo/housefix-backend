package eu.bsinfo.web.api;

import eu.bsinfo.db.DatabaseConnection;
import eu.bsinfo.web.Server;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/setupDB")
public class SetupDBController {
    private final DatabaseConnection dbConn = Server.getDbConn();

    @DELETE
    public Response delete() {
        dbConn.removeAllTables();
        dbConn.createAllTables();
        return Response.ok().build();
    }
}

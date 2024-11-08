package eu.bsinfo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import eu.bsinfo.db.DatabaseConnection;
import eu.bsinfo.utils.LoggingProvider;
import eu.bsinfo.web.exceptions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static HttpServer server;
    private static DatabaseConnection dbConn;

    public static void startServer(String url, DatabaseConnection databaseConnection) {
        try {
            dbConn = databaseConnection;
            dbConn.openConnection();
            dbConn.createAllTables();
            Runtime.getRuntime().addShutdownHook(new Thread(dbConn::closeConnection));

            URI uri = URI.create(url);
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());

            ResourceConfig config = new ResourceConfig()
                    .packages("eu.bsinfo.web.api")
                    .register(LoggingProvider.class)
                    .register(new JacksonJsonProvider(objectMapper))
                    .register(InvalidFormatExceptionMapper.class)
                    .register(NotFoundExceptionMapper.class)
                    .register(ValueInstantiationExceptionMapper.class)
                    .register(DateTimeParseExceptionMapper.class)
                    .property(ServerProperties.WADL_FEATURE_DISABLE, true);

            server = JdkHttpServerFactory.createHttpServer(uri, config);
            LOGGER.log(Level.INFO, "ready - started server on "+url+", url: http://localhost:"+uri.getPort());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            server = null;

            dbConn.closeConnection();
            dbConn = null;

            LOGGER.log(Level.INFO, "server stopped");
        }
    }

    @Nullable
    public static DatabaseConnection getDbConn() {
        return dbConn;
    }
}

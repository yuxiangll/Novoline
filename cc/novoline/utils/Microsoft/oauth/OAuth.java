package cc.novoline.utils.Microsoft.oauth;

import cc.novoline.Novoline;
import cc.novoline.utils.Microsoft.oauth.http.HttpResponse;
import cc.novoline.utils.Microsoft.oauth.utils.ColUtils;
import cc.novoline.utils.Microsoft.oauth.utils.HttpUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static cc.novoline.utils.notifications.NotificationType.INFO;
import static cc.novoline.utils.notifications.NotificationType.SUCCESS;

/**
 * @author TIMER_err
 */
public class OAuth {
    private static final String CLIENT_ID = "baeb6344-8129-4340-b9a7-d72d4e8d36d3";

    private static String readResponse(HttpResponse response) {
        return HttpUtils.getStringFromInputStream(new ByteArrayInputStream(response.body));
    }

    public static void login(LoginCallback callback) {
        try {
            String authorize = HttpUtils.buildUrl("https://login.live.com/oauth20_authorize.srf", ColUtils.mapOf(
                    "client_id", CLIENT_ID,
                    "response_type", "code",
                    "redirect_url", "http://127.0.0.1:30828",
                    "scope", "XboxLive.signin offline_access"
            ));
            System.out.println(authorize);
            Desktop.getDesktop().browse(new URI(authorize));

            //Let's get the Code
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(30828), 0);
            httpServer.createContext("/", httpExchange -> {
                JsonParser parser = new JsonParser();
                String oauth_access_token =
                        //Parse the Response
                        parser.parse(
                                readResponse(
                                        //Let's get the Token
                                        HttpUtils.getEngine().postForm(
                                                "https://login.live.com/oauth20_token.srf", ColUtils.mapOf(
                                                        "client_id", CLIENT_ID,
                                                        "code", httpExchange.getRequestURI().toString().
                                                                substring(
                                                                        httpExchange.getRequestURI().toString().
                                                                                lastIndexOf('=') + 1
                                                                ),
                                                        "grant_type", "authorization_code",
                                                        "redirect_url", "http://127.0.0.1:30828"
                                                )
                                        )
                                )
                        ).getAsJsonObject().get("access_token").getAsString();

                System.out.println("OAuthToken");
                Novoline.getInstance().getNotificationManager().pop("Trying to login...", INFO);

                // YolBi.INSTANCE.notificationsManager.add(new Info("Trying to login...", Notification.Type.Info));


                //The XBL
                //noinspection HttpUrlsUsage
                JsonObject xbl = HttpUtils.gson().fromJson(readResponse(
                                HttpUtils.getEngine().postJson(
                                        "https://user.auth.xboxlive.com/user/authenticate",
                                        HttpUtils.gson(), ColUtils.mapOf(
                                                "Properties", ColUtils.mapOf(
                                                        "AuthMethod", "RPS",
                                                        "SiteName", "user.auth.xboxlive.com",
                                                        "RpsTicket", "d=" + oauth_access_token
                                                ),
                                                "RelyingParty", "http://auth.xboxlive.com",
                                                "TokenType", "JWT"
                                        )
                                )
                        ), JsonObject.class
                );

                System.out.println("XBOX Live");

                String xbl_token = xbl.get("Token").getAsString();

                //The XSTS
                JsonObject xsts = HttpUtils.gson().fromJson(readResponse(
                                HttpUtils.getEngine().postJson(
                                        "https://xsts.auth.xboxlive.com/xsts/authorize",
                                        HttpUtils.gson(), ColUtils.mapOf(
                                                "Properties", ColUtils.mapOf(
                                                        "SandboxId", "RETAIL",
                                                        "UserTokens", new String[]{xbl_token}
                                                ),
                                                "RelyingParty", "rp://api.minecraftservices.com/",
                                                "TokenType", "JWT"
                                        )
                                )
                        ), JsonObject.class
                );

                System.out.println("XSTS");

                String xsts_token = xsts.get("Token").getAsString();
                String xsts_uhs = xsts.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();

                //Login with XBOX
                JsonObject xbox = HttpUtils.gson().fromJson(readResponse(
                                HttpUtils.getEngine().postJson(
                                        "https://api.minecraftservices.com/authentication/login_with_xbox",
                                        HttpUtils.gson(), ColUtils.mapOf(
                                                "identityToken", String.format("XBL3.0 x=%s;%s", xsts_uhs, xsts_token)
                                        )
                                )
                        ), JsonObject.class
                );

                System.out.println("Login XBOX");

                //Get Minecraft profile!
                JsonObject profile = HttpUtils.gson().fromJson(readResponse(
                                HttpUtils.getEngine().getJson(
                                        "https://api.minecraftservices.com/minecraft/profile", ColUtils.mapOf(
                                                "Authorization", "Bearer " + xbox.get("access_token").getAsString()
                                        )
                                )
                        ), JsonObject.class
                );

                System.out.println("GetProfile");
                Novoline.getInstance().getNotificationManager().pop("Logged in as " + profile.get("name").getAsString(), SUCCESS);

                // YolBi.INSTANCE.notificationsManager.add(new Info("Logged in as " + profile.get("name").getAsString(), Notification.Type.Success));
                callback.run(
                        profile.get("name").getAsString(),
                        profile.get("id").getAsString().replace("-", ""),
                        xbox.get("access_token").getAsString(), true
                );
                String success = "Login successfully, you can close this page now!";
                httpExchange.sendResponseHeaders(200, success.length());
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.write(success.getBytes(StandardCharsets.UTF_8));
                responseBody.close();

                httpServer.stop(2);
                // Alt alt = new Alt(Minecraft.getMinecraft().session.getUsername(),Minecraft.getMinecraft().session.getToken(),Minecraft.getMinecraft().session.getPlayerID());
                // YolBi.INSTANCE.getAltManager().addAlt(alt);
                // GuiAltManager.microsoftlogin = false;
            });
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

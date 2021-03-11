package com.syakeapps.twe.jaxrs.endpoint;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syakeapps.twe.jaxrs.model.DiscordWebhookPayload;
import com.syakeapps.twe.jaxrs.model.DiscordWebhookPayload.Embed;
import com.syakeapps.twe.jaxrs.model.DiscordWebhookPayload.Embed.Author;
import com.syakeapps.twe.jaxrs.model.DiscordWebhookPayload.Embed.Field;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Path("/streamchanged")
public class StreamChanged {
    private static final String PROPERTY_KEY_DISCORD_WEBHOOK_ENDPOINT = "TWITCH_WEBHOOK_ENDPOINT";
    private static final okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamChanged.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String challenge(@QueryParam(value = "hub.challenge") String challenge) {
        return challenge;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String streamChanged(Map<String, Object> requestBody) {
        final List<Map<String, Object>> data = (List<Map<String, Object>>) requestBody.get("data");

        try {
            String url = System.getenv(PROPERTY_KEY_DISCORD_WEBHOOK_ENDPOINT);
            if (data.size() > 0) {
                Map<String, Object> datum = data.get(0);
                final String userName = (String) datum.get("user_name");
                final String gameName = (String) datum.get("game_name");
                final String startAt = ZonedDateTime
                        .parse((String) datum.get("started_at"),
                                DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC")))
                        .withZoneSameInstant(ZoneId.of("Asia/Tokyo"))
                        .format(DateTimeFormatter.ofPattern("M月dd日(E) HH:mm").withLocale(Locale.JAPANESE)).toString();
                final String userLogin = (String) datum.get("user_login");
                final String channelUrl = "https://www.twitch.tv/" + userLogin;

                Author author = new Author(userName + " is now streaming");
                Field playing = new Field("Playing", gameName, true);
                Field startedAt = new Field("Started at (Asia/Tokyo)", startAt, true);
                Embed embed = new Embed(channelUrl, channelUrl, 6570404, null, author,
                        Arrays.asList(playing, startedAt));
                DiscordWebhookPayload payload = new DiscordWebhookPayload(userName + "がTwitchで配信を始めました",
                        Arrays.asList(embed));
                String json = new ObjectMapper().writeValueAsString(payload);

                try (Response response = post(url, JSON, json)) {
                    if (!response.isSuccessful()) {
                        throw new IOException("UNEXPECTED_RESPONSE=>" + response + ", DISCORD_PAYLOAD=>" + json);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR=>", e);
        }

        return "DONE";
    }

    private Response post(String url, okhttp3.MediaType type, String body) throws IOException {
        Request request = new Request.Builder().url(url).post(RequestBody.create(JSON, body)).build();
        return callExternalAPI(request);
    }

    private Response callExternalAPI(Request request) throws IOException {
        return new OkHttpClient().newCall(request).execute();
    }
}

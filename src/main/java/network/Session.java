package network;

import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created Session.java in network
 * by Arne on 11.01.2017.
 */
public class Session {
    private final org.eclipse.jetty.websocket.api.Session session;
    private final HashMap<String, String> urlParams;

    public Session(org.eclipse.jetty.websocket.api.Session session) {
        this.session = session;
        this.urlParams = parseURLParams(((WebSocketSession) session).getRequestURI().getQuery());
    }

    private HashMap<String, String> parseURLParams(String query) {
        HashMap<String, String> urlParams = new HashMap<>();
        for(String param : query.split("&")) {
            if(param.contains("=")) {
                String[] split = param.split("=");
                urlParams.put(split[0], split[1]);
            } else {
                urlParams.put(param, null);
            }
        }

        return urlParams;
    }

    public String getQueryParam(String key) {
        return urlParams.get(key);
    }

    public void sendMessage(JSONObject json) {
        sendMessage(json.toString());
    }

    private void sendMessage(String string) {
        try {
            session.getRemote().sendString(string);
        } catch (Exception e) {
            System.err.println("Can't send message to Client");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session1 = (Session) o;

        return session.equals(session1.session);
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

    public org.eclipse.jetty.websocket.api.Session getSession() {
        return session;
    }
}

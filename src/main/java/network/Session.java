package network;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created Session.java in network
 * by Arne on 11.01.2017.
 */
public class Session {
    private final org.eclipse.jetty.websocket.api.Session session;

    public Session(org.eclipse.jetty.websocket.api.Session session) {
        this.session = session;
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

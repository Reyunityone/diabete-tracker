package application.classiGeneriche;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {
    @AfterEach
    void tearDown(){
        Session.getInstance().logout();
    }

    @Test
    void getInstanceStessaIstanza(){
        Session s1 = Session.getInstance();
        Session s2 = Session.getInstance();
        assertSame(s1, s2);
    }

    @Test
    void funzionamentoSetELogout(){
        Diabetologo d = new Diabetologo();
        Session.getInstance().setCurrentUser(d);
        assertEquals(d, Session.getInstance().getCurrentUser());
        Session.getInstance().logout();
        assertNull(Session.getInstance().getCurrentUser());
    }


}
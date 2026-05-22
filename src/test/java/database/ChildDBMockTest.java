package database;

import dao.ChildDAO;
import dao.exception.DatabaseException;
import model.Child;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.ChildService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mock demo for child flow")
class ChildDBMockTest {

    @Mock
    private ChildDAO childDAO;

    @InjectMocks
    private ChildService childService;

    @Test
    @DisplayName("Mock: addChild delegates to DAO")
    void addChildShouldDelegateToDao() throws SQLException {

        Child input = new Child("Kevin", "McCallister",
                LocalDate.of(2010,1,1));

        Child expected = new Child(10L, "Kevin", "McCallister",
                LocalDate.of(2010,1,1));

        when(childDAO.addChild(input)).thenReturn(expected);

        Child result = childService.addChild(input);

        assertEquals(10L,result.id());

        verify(childDAO).addChild(input);
    }

    @Test
    @DisplayName("Mock: addChild validates null")
    void addChildShouldValidateNull() throws SQLException{

        assertThrows(IllegalArgumentException.class, ()-> childService.addChild(null));

        verify(childDAO,never()).addChild(any());
    }

    @Test
    @DisplayName("Mock: addChild translates SQLException")
    void addChildShouldTranslateException() throws SQLException {

        Child child = new Child("Kevin", "McCallister", null);

        when(childDAO.addChild(any())).thenThrow(new SQLException("DataBase Error"));

        assertThrows(DatabaseException.class, ()-> childService.addChild(child));
    }

    @Test
    @DisplayName("Mock: update delegates")
    void updateShouldDelegate() throws SQLException {

        Child child = new Child(1L, "Updated", "McCallister", null);

        when(childDAO.updateChild(any())).thenReturn(true);

        boolean result = childService.updateChild(child);

        assertTrue(result);

        verify(childDAO).updateChild(child);
    }

    @Test
    @DisplayName("Mock: update throws exception when child not found")
    void updateShouldThrow() throws SQLException {

        Child child = new Child(1L, "Updated", "McCallister", null);

        when(childDAO.updateChild(any())).thenReturn(false);

        assertThrows(DatabaseException.class, ()-> childService.updateChild(child));
    }

    @Test
    @DisplayName("Mock: delete delegates")
    void deleteShouldDelegate() throws SQLException {

        when(childDAO.deleteChild(1L)).thenReturn(true);

        boolean result = childService.deleteChild(1L);

        assertTrue(result);

        verify(childDAO).deleteChild(1L);
    }

    @Test
    @DisplayName("Mock: delete throws exception when missing")
    void deleteShouldThrow() throws SQLException {

        when(childDAO.deleteChild(any())).thenReturn(false);

        assertThrows(DatabaseException.class, ()-> childService.deleteChild(1L));
    }

    @Test
    @DisplayName("Mock: find older children")
    void findOlderChildrenShouldReturnData() throws SQLException {

        when(childDAO.findChildrenWithMinimumAge(10)).thenReturn(
        List.of(new Child(1L, "Kevin", "McCallister",
                        LocalDate.of(
                                2010,
                                1,
                                1))));

        List<Child> result = childService.findOlderChildren(10);

        assertEquals(1, result.size());

        verify(childDAO).findChildrenWithMinimumAge(10);
    }

    @Test
    @DisplayName("Mock: negative age validation")
    void ageShouldValidate(){

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> childService.findOlderChildren(-1));

        assertEquals("Minimum age cannot be negative", ex.getMessage());
    }

    @Test
    @DisplayName("Mock: find children without birth date")
    void missingBirthDateShouldReturnData() throws SQLException {

        when(childDAO.findChildrenWithoutBirthDate()).thenReturn(List.of(new Child(1L,
                                        "Thomas",
                                        "Shelby",
                                        null)));

        List<Child> result = childService.findChildrenMissingBirthDate();

        assertEquals(1, result.size());

        assertNull(result.getFirst().birthDate());
    }
}

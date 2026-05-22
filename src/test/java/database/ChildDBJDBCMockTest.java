package database;

import dao.ChildDB;
import model.Child;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JDBC-level mock tests for ChildDB")
class ChildDBJDBCMockTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSet generatedKeys;

    private ChildDB childDB;

    @BeforeEach
    void setUp() {
        childDB = new ChildDB(connection);
    }

    @Test
    @DisplayName("addChild: inserts child successfully")
    void addChildShouldInsertSuccessfully() throws SQLException {

        Child input = new Child("Kevin", "McCallister", LocalDate.of(2015,1,1));

        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);

        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);

        when(generatedKeys.next()).thenReturn(true);

        when(generatedKeys.getLong(1)).thenReturn(10L);

        Child result = childDB.addChild(input);

        assertEquals(10L, result.id());
        assertEquals("Kevin", result.firstName());

        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("addChild: throws if generated key is missing")
    void addChildShouldThrowWhenGeneratedKeyMissing() throws SQLException {

        Child input = new Child("John","Doe",null);

        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);

        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);

        when(generatedKeys.next()).thenReturn(false);

        assertThrows(SQLException.class, ()-> childDB.addChild(input));
    }

    @Test
    @DisplayName("updateChild returns true")
    void updateChildShouldReturnTrue() throws SQLException {

        Child child = new Child(1L, "Updated", "Kevin", null);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(childDB.updateChild(child));
    }

    @Test
    @DisplayName("deleteChild returns false")
    void deleteShouldReturnFalse() throws SQLException {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(childDB.deleteChild(100L));
    }

    @Test
    @DisplayName("findChildrenWithMinimumAge returns list")
    void findChildrenShouldReturnList() throws SQLException {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true,true,false);

        when(resultSet.getLong("id")).thenReturn(1L,2L);

        when(resultSet.getString("first_name")).thenReturn("Kevin","Anna");

        when(resultSet.getString("last_name")).thenReturn("McCallister","Smith");

        when(resultSet.getDate("birth_date")).thenReturn(Date.valueOf("2010-01-01"), Date.valueOf("2012-02-02"));

        List<Child> children = childDB.findChildrenWithMinimumAge(10);

        assertEquals(2, children.size());
        assertEquals("Kevin", children.getFirst().firstName()
        );
    }

    @Test
    @DisplayName("addChild(null) validation")
    void addShouldValidateNull() {
        assertThrows(IllegalArgumentException.class, () -> childDB.addChild(null));
    }

}
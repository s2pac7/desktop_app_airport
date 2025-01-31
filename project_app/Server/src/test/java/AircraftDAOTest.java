import Pojo.Aircraft;
import dao.AircraftDAO;
import dao.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AircraftDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private AircraftDAO aircraftDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aircraftDAO = new AircraftDAO();
        DBConnection mockDbConnection = mock(DBConnection.class);

        try {
            when(mockDbConnection.getDbConnection()).thenReturn(mockConnection);
        } catch (ClassNotFoundException | SQLException e) {
            fail("������ ��� ��������� ������: " + e.getMessage());
        }

        aircraftDAO.setDbConnection(mockDbConnection); // ���������� ������
    }

    @Test
    void testAddAircraft() throws SQLException, ClassNotFoundException {
        Aircraft aircraft = new Aircraft(1, "Boeing 737", "Commercial");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = aircraftDAO.addAircraft(aircraft);

        assertTrue(result, "����� addAircraft ������ ���������� true ��� �������� �������");
        verify(mockPreparedStatement).setString(1, "Boeing 737");
        verify(mockPreparedStatement).setString(2, "Commercial");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteAircraft() throws SQLException, ClassNotFoundException {
        int aircraftId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = aircraftDAO.deleteAircraft(aircraftId);

        assertTrue(result, "����� deleteAircraft ������ ���������� true ��� �������� ��������");
        verify(mockPreparedStatement).setInt(1, aircraftId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testSearchAircraftsByName() throws SQLException, ClassNotFoundException {
        String searchQuery = "Boeing";
        Aircraft mockAircraft = new Aircraft(1, "Boeing 737", "Commercial");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(mockAircraft.getId());
        when(mockResultSet.getString("nameAircraft")).thenReturn(mockAircraft.getAircraftName());
        when(mockResultSet.getString("typeAircraft")).thenReturn(mockAircraft.getAircraftType());

        var result = aircraftDAO.searchAircraftsByName(searchQuery);

        assertNotNull(result, "������ �� ������ ���� null");
        assertEquals(1, result.size(), "������ ������ ��������� 1 �������");
        assertEquals(mockAircraft.getAircraftName(), result.get(0).getAircraftName(), "����� ��������� ������ ���������");
        verify(mockPreparedStatement).setString(1, "%Boeing%");
    }
}

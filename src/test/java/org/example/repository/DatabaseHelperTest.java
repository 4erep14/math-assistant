package org.example.repository;

import org.example.repository.DatabaseHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseHelperTest {

    private static MockedStatic<DriverManager> mockedDriverManager;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private Statement mockStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private Array mockArray;

    @BeforeAll
    public static void init() {
        mockedDriverManager = mockStatic(DriverManager.class);
    }

    @BeforeEach
    void setUp() throws Exception {
        lenient().when(mockConnection.createStatement()).thenReturn(mockStatement);
    }

    @AfterEach
    public void resetStaticMocks() {
        mockedDriverManager.reset();
    }

    @AfterAll
    public static void tearDown() {
        mockedDriverManager.close();
    }

    private void prepareForSaveEquation() throws SQLException {
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    private void prepareForSaveRoot() throws SQLException {
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    }

    private void prepareForFindEquationsByRoots(List<Double> roots) throws SQLException {
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockConnection.createArrayOf(anyString(), any(Object[].class))).thenReturn(mockArray);
    }

    private void prepareForFindUnsolvedEquations() throws SQLException {
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("equation")).thenReturn("x*2 + 4 = 0", "x*3 - 1 = 0");
    }

    @Test
    void staticInitialization_FailedConnection_throwSQLException() throws SQLException {
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenThrow(SQLException.class);

        assertThrows(SQLException.class, () -> DatabaseHelper.saveEquation("x*2 + 4 = 0"));
    }

    @Test
    void saveEquation_ValidEquation_SavesSuccessfully() throws SQLException {
        prepareForSaveEquation();
        String validEquation = "x*2 + 2 = 0";

        DatabaseHelper.saveEquation(validEquation);

        verify(mockPreparedStatement).setString(1, validEquation);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void saveEquation_InvalidEquation_ThrowsIllegalArgumentException() {
        String invalidEquation = "invalid";

        assertThrows(IllegalArgumentException.class, () -> DatabaseHelper.saveEquation(invalidEquation));
    }

    @Test
    void saveRoot_CorrectRoot_SavesSuccessfully() throws SQLException{
        prepareForSaveRoot();

        String equation = "x*2 - 4 = 0";
        double root = 2.0;

        DatabaseHelper.saveRoot(equation, root);

        verify(mockPreparedStatement).setString(1, equation);
        verify(mockPreparedStatement).setDouble(2, root);

        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void saveRoot_NullRootValue_ThrowsIllegalArgumentException() throws SQLException {
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);

        assertThrows(IllegalArgumentException.class, () -> DatabaseHelper.saveRoot("x*2 - 4 = 0", null));
    }

    @Test
    void findEquationsByRoots_WithMatchingRoots_ReturnsEquations() throws SQLException {
        List<Double> roots = Arrays.asList(1.0, -2.0);
        prepareForFindEquationsByRoots(roots);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("equation")).thenReturn("x*2 - 1 = 0", "x*2 + 1 = 0");

        List<String> equations = DatabaseHelper.findEquationsByRoots(roots);

        assertEquals(2, equations.size());
        assertTrue(equations.contains("x*2 - 1 = 0"));
        assertTrue(equations.contains("x*2 + 1 = 0"));
    }

    @Test
    void findEquationsByRoots_WithNoMatchingRoots_ReturnsEmptyList() throws SQLException {
        List<Double> roots = Arrays.asList(3.0, 4.0);
        prepareForFindEquationsByRoots(roots);

        when(mockResultSet.next()).thenReturn(false);

        List<String> equations = DatabaseHelper.findEquationsByRoots(roots);

        assertTrue(equations.isEmpty());
    }

    @Test
    void findUnsolvedEquations_ReturnsListOfEquations() throws SQLException {
        prepareForFindUnsolvedEquations();

        List<String> unsolvedEquations = DatabaseHelper.findUnsolvedEquations();

        assertEquals(2, unsolvedEquations.size());
        assertEquals(List.of("x*2 + 4 = 0", "x*3 - 1 = 0"), unsolvedEquations);
    }
}

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FrequencyAnalysisTest {

    @Test
    @DisplayName("Подсчет частот для массива строк")
    void testFrequencyAnalysisStrings() {
        String[] arr = {"a", "b", "a", "c", "b", "a"};
        Map<String, Integer> result = frequencyAnalysis(arr);

        assertEquals(3, result.size());
        assertEquals(3, result.get("a"));
        assertEquals(2, result.get("b"));
        assertEquals(1, result.get("c"));
    }

    @Test
    @DisplayName("Подсчет частот для массива чисел")
    void testFrequencyAnalysisIntegers() {
        Integer[] arr = {1, 2, 2, 3, 3, 3};
        Map<Integer, Integer> result = frequencyAnalysis(arr);

        assertEquals(3, result.size());
        assertEquals(1, result.get(1));
        assertEquals(2, result.get(2));
        assertEquals(3, result.get(3));
    }

    @Test
    @DisplayName("Массив из одного элемента")
    void testSingleElement() {
        String[] arr = {"only"};
        Map<String, Integer> result = frequencyAnalysis(arr);

        assertEquals(1, result.size());
        assertEquals(1, result.get("only"));
    }

    @Test
    @DisplayName("Пустой массив возвращает пустую мапу")
    void testEmptyArray() {
        String[] arr = {};
        Map<String, Integer> result = frequencyAnalysis(arr);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("null массив возвращает пустую мапу")
    void testNullArray() {
        Map<String, Integer> result = frequencyAnalysis(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Все элементы одинаковые")
    void testAllSameElements() {
        Integer[] arr = {5, 5, 5, 5, 5};
        Map<Integer, Integer> result = frequencyAnalysis(arr);

        assertEquals(1, result.size());
        assertEquals(5, result.get(5));
    }

    @Test
    @DisplayName("Все элементы уникальные")
    void testAllUniqueElements() {
        Integer[] arr = {1, 2, 3, 4, 5};
        Map<Integer, Integer> result = frequencyAnalysis(arr);

        assertEquals(5, result.size());
        for (int i = 1; i <= 5; i++) {
            assertEquals(1, result.get(i));
        }
    }

    @Test
    @DisplayName("Массив с null элементами")
    void testArrayWithNulls() {
        String[] arr = {"a", null, "b", null, null};
        Map<String, Integer> result = frequencyAnalysis(arr);

        assertEquals(3, result.size());
        assertEquals(1, result.get("a"));
        assertEquals(1, result.get("b"));
        assertEquals(3, result.get(null));
    }

    @Test
    @DisplayName("Массив только из null элементов")
    void testArrayOnlyNulls() {
        String[] arr = {null, null, null};
        Map<String, Integer> result = frequencyAnalysis(arr);

        assertEquals(1, result.size());
        assertEquals(3, result.get(null));
    }

    @Test
    @DisplayName("Работа с нестроковыми объектами (кастомный класс)")
    void testCustomObjects() {
        // Используем Integer как пример объекта с equals/hashCode
        Integer[] arr = {1000, 2000, 1000, 3000, 2000, 1000};
        Map<Integer, Integer> result = frequencyAnalysis(arr);

        assertEquals(3, result.size());
        assertEquals(3, result.get(1000));
        assertEquals(2, result.get(2000));
        assertEquals(1, result.get(3000));
    }

    @Test
    @DisplayName("Символы (Character)")
    void testCharacters() {
        Character[] arr = {'a', 'b', 'a', 'c', 'a', 'b'};
        Map<Character, Integer> result = frequencyAnalysis(arr);

        assertEquals(3, result.size());
        assertEquals(3, result.get('a'));
        assertEquals(2, result.get('b'));
        assertEquals(1, result.get('c'));
    }

    @Test
    @DisplayName("Возвращаемая мапа изменяемая")
    void testReturnedMapIsMutable() {
        Integer[] arr = {1, 2, 3};
        Map<Integer, Integer> result = frequencyAnalysis(arr);

        // Проверяем, что мапа не unmodifiable
        assertDoesNotThrow(() -> result.put(4, 1));
        assertEquals(1, result.get(4));
    }

    @Test
    @DisplayName("Проверка значений по умолчанию для отсутствующих ключей")
    void testMissingKeyReturnsNull() {
        String[] arr = {"a", "b"};
        Map<String, Integer> result = frequencyAnalysis(arr);

        assertNull(result.get("c"));
        assertFalse(result.containsKey("c"));
    }

    @Test
    @DisplayName("Большой массив для проверки производительности и корректности")
    void testLargeArray() {
        Integer[] arr = new Integer[10000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i % 100; // каждое число от 0 до 99 встречается 100 раз
        }

        Map<Integer, Integer> result = frequencyAnalysis(arr);

        assertEquals(100, result.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(100, result.get(i));
        }
    }

    @Test
    @DisplayName("Порядок элементов не влияет на результат")
    void testOrderIndependence() {
        Integer[] arr1 = {1, 2, 3, 2, 1};
        Integer[] arr2 = {3, 1, 2, 1, 2};

        Map<Integer, Integer> result1 = frequencyAnalysis(arr1);
        Map<Integer, Integer> result2 = frequencyAnalysis(arr2);

        assertEquals(result1, result2);
    }

    // Вспомогательный метод — предполагается, что frequencyAnalysis находится в том же классе
    // или в статическом импорте. Если метод в другом классе, добавьте импорт.
    private static <T> Map<T, Integer> frequencyAnalysis(T[] arr) {
        return org.example.Main.frequencyAnalysis(arr);
    }
}
package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.service.BookService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.*;
import java.util.stream.Collectors;

public class MyESBookServiceImpl implements BookService {
    @PersistenceContext
    private EntityManager entityManager;
    private static final String GET_BOOKS = "SELECT g.genre_name, COUNT(bg.book_id) AS book_count\n" +
            "FROM genres g\n" +
            "         JOIN book_genres bg ON g.genre_name = bg.genre\n" +
            "         JOIN books b ON bg.book_id = b.id\n" +
            "GROUP BY g.genre_name";
    private static final String GET_ALL_BY_CRITERIA = "SELECT DISTINCT b.id, b.title, b.author, b.description, b.publication_date FROM books b INNER JOIN book_genres bg ON b.id = bg.book_id WHERE 1=1";
    private static final String TITLE = "title LIKE ?";
    private static final String AUTHOR = "author LIKE ?";
    private static final String DESCRIPTION = "description LIKE ?";
    private static final String YEAR = "EXTRACT(YEAR FROM publication_date) = CAST(? AS integer)";
    private static final String GENRE = "bg.genre LIKE ?";
    private static final String SORT_DATE = " ORDER BY publication_date DESC";
    private static final String OR = "OR";
    private static final String AND = "AND";
    private static final String PERCENT = "%";

    /*method retrieves the total count of books for each genre,
    ordered from the genre with the most books to the least.*/
    @Override
    public Map<String, Long> getBooks() {
        Query query = entityManager.createNativeQuery(GET_BOOKS);
        List<Object[]> results = query.getResultList();
        Map<String, Long> genreBookCountMap = new HashMap<>();
        for (Object[] result : results) {
            String genreName = (String) result[0];
            Long bookCount = ((Number) result[1]).longValue();
            genreBookCountMap.put(genreName, bookCount);
        }
        return genreBookCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // sorting reverse order
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /*method returns books matching the provided search criteria,
    sorted by publication date.*/
    @Override
    public List<Book> getAllByCriteria(SearchCriteria searchCriteria) {
        StringBuilder sql = new StringBuilder(GET_ALL_BY_CRITERIA);
        List<String> conditions = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        if (searchCriteria.getTitle() != null && !searchCriteria.getTitle().isEmpty()) {
            conditions.add(TITLE);
            parameters.add(PERCENT + searchCriteria.getTitle() + PERCENT);
        }
        if (searchCriteria.getAuthor() != null && !searchCriteria.getAuthor().isEmpty()) {
            conditions.add(AUTHOR);
            parameters.add(PERCENT + searchCriteria.getAuthor() + PERCENT);
        }
        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            conditions.add(DESCRIPTION);
            parameters.add(PERCENT + searchCriteria.getDescription() + PERCENT);
        }
        if (searchCriteria.getYear() != null) {
            String year = String.valueOf(searchCriteria.getYear());
            conditions.add(YEAR);
            parameters.add(year);
        }
        if (searchCriteria.getGenre() != null && !searchCriteria.getGenre().isEmpty()) {
            conditions.add(GENRE);
            parameters.add(PERCENT + searchCriteria.getGenre() + PERCENT);
        }
        if (!conditions.isEmpty()) {
            sql.append(AND).append(String.join(OR, conditions));
        }
        sql.append(SORT_DATE);
        Query query = entityManager.createNativeQuery(sql.toString(), Book.class);
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
        }
        return query.getResultList();
    }

    /*I created this class to implement the BookService interface
 and place my implementation in the existing impl package*/
}

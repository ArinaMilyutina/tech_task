package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MyESMemberServiceImpl implements MemberService {
    @PersistenceContext
    private EntityManager entityManager;
    private final String FIND_MEMBERS = "SELECT m.id, m.name, m.membership_date\n" +
            "FROM members m\n" +
            "         LEFT JOIN member_books mb ON m.id = mb.member_id\n" +
            "WHERE EXTRACT(YEAR FROM m.membership_date) = 2023\n" +
            "  AND mb.book_id IS NULL;";
    private final String FIND_MEMBER = "SELECT m.name,\n" +
            "       m.membership_date,\n" +
            "    m.id\n" +
            "\n" +
            "FROM members m\n" +
            "         JOIN\n" +
            "     member_books mb ON m.id = mb.member_id\n" +
            "         JOIN\n" +
            "     books b ON mb.book_id = b.id\n" +
            "         JOIN\n" +
            "     book_genres bg ON b.id = bg.book_id\n" +
            "WHERE bg.genre = 'Romance'\n" +
            "  AND b.publication_date = (SELECT MIN(b2.publication_date)\n" +
            "                            FROM books b2\n" +
            "                                     JOIN\n" +
            "                                 book_genres bg2 ON b2.id = bg2.book_id\n" +
            "                            WHERE bg2.genre = 'Romance')\n" +
            "ORDER BY m.membership_date DESC\n" +
            "LIMIT 1";

    /* method that finds and returns the member
    who has read the oldest “Romance” genre book
    and who was most recently registered on the platform.
     (If we want to return member without an id or other parameter,
     it is better to use MemberDto, but it is FORBIDDEN to make any changes in MemberService)*/
    @Override
    public Member findMember() {
        Query query = entityManager.createNativeQuery(FIND_MEMBER, Member.class);
        return (Member) query.getSingleResult();
    }

    /* method that finds and returns members
    who registered in 2023 but have not read any books.*/
    @Override
    public List<Member> findMembers() {
        return (List<Member>) entityManager.createNativeQuery(FIND_MEMBERS, Member.class).getResultList();
    }
}

/*I created this class to implement the MemberService interface
 and place your implementation in the existing impl package*/
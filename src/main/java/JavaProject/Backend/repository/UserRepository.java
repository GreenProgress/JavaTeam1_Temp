package JavaProject.Backend.repository;

import JavaProject.Backend.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 * 
 * User 컬렉션에 대한 CRUD 및 쿼리 메서드 정의
 * 
 * @author JP Team
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {


    Optional<User> findByUserId(String userId);
    // 사용자 ID로 조회
    // @param userId 사용자 아이디
    // @return Optional<User>


    boolean existsByUserId(String userId); // 사용자 ID 존재 여부 확인
    // @param userId 사용자 아이디
    // @return true: 존재, false: 없음


    Optional<User> findByUserIdAndActiveTrue(String userId); // 활성 사용자 조회
    // @param userId 사용자 아이디
    // @return Optional<User>
    
    
}

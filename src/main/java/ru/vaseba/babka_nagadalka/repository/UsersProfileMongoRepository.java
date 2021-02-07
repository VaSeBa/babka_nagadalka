package ru.vaseba.babka_nagadalka.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.vaseba.babka_nagadalka.model.UserProfileData;


@Repository
public interface UsersProfileMongoRepository extends MongoRepository<UserProfileData, String> {
    UserProfileData findByChatId(long chatId);
    void deleteByChatId(long chatId);
}

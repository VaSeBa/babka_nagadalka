package ru.vaseba.babka_nagadalka.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "userProfileData")
public class UserProfileData implements Serializable {
    @Id
    String id;
//    String name;
//    String gender;
//    String color;
//    String movie;
//    String song;
//    int age;
//    int number;
    String whatIfYes;
    String whatIfNo;
    String whatNoIfDo;
    String whatNoIfNotDo;
    long chatId;



    @Override
    public String toString() {
        return String.format("Что будет, если это произойдет: %s%nЧто будет, если это не произойдет: %s%nЧего не будет, если это произойдет: %s%nЧего не будет, если это не произойдет: %s%n"
                , getWhatIfYes(), getWhatIfNo(), getWhatNoIfDo(), getWhatNoIfNotDo());
    }


//    @Override
//    public String toString() {
//        return String.format("Что будет, если это произойдет: %s%nЧто будет, если это не произойдет: %s%nЧего не будет, если это произойдет: %s%nЧего не будет, если это не произойдет: %s%n"
//                , getWhatIfYes(), getWhatIfNo(), getWhatNoIfDo(), getWhatNoIfNotDo());
//    }
}

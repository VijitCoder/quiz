package engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Костыли:
 * Jackson не умеет извлекать массив из запроса. Обязательно нужно в какой-то объект обернуть поле,
 * в которое Jackson положит массив из запроса. Но логике приложения этот объект нахрен не нужен.
 */
public class AnswerDto {
    @JsonProperty
    private int[] answer;

    public int[] getAnswer() {
        return answer;
    }
}
package me.sup2is.member.domain.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MemberRequestDtoTest {

    @Test
    public void validation_test() {
        //given
        MemberRequestDto memberRequestDto =
                new MemberRequestDto("my-validate"
                        ,"1"
                        , ""
                        , "test"
                        , 123
                        , "12341234");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Map<String, String> expect = new HashMap<>();
        expect.put("name", "name must not be null");
        expect.put("phone", "phone format is 010-xxxx-xxxx");
        expect.put("email", "invalid email format");
        expect.put("password", "password must be greater than 6");

        //when
        Set<ConstraintViolation<MemberRequestDto>> validate = validator.validate(memberRequestDto);

        //then

        assertFalse(validate.isEmpty());
        assertEquals(validate.size(), expect.size());
        for (ConstraintViolation<MemberRequestDto> memberRequestDtoConstraintViolation : validate) {

            if(!expect.containsKey(memberRequestDtoConstraintViolation.getPropertyPath().toString()))
                fail();

            String expectMessage = expect.get(memberRequestDtoConstraintViolation.getPropertyPath().toString());
            assertEquals(expectMessage, memberRequestDtoConstraintViolation.getMessage());

        }

    }

}
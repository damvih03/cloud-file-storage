package com.damvih.authentication.docs;

import com.damvih.authentication.dto.UserResponseDto;
import com.damvih.common.openapi.InternalServerErrorApiResponse;
import com.damvih.common.web.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation
@InternalServerErrorApiResponse
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "username": "user"
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(responseCode = "401", description = "Bad credentials",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "message": "Invalid username or password."
                                        }
                                        """
                        )
                )
        ),
})
public @interface SignInDocs {
}

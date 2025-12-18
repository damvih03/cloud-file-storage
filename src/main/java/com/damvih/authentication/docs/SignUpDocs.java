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
        @ApiResponse(responseCode = "201", description = "Created",
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
        @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "message": "Username should contain between 5 and 20 characters."
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(responseCode = "409", description = "Username already exists",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "message": "User already exists."
                                        }
                                        """
                        )
                )
        )
})
public @interface SignUpDocs {
}

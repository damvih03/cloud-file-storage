package com.damvih.storage.docs;

import com.damvih.common.openapi.InternalServerErrorApiResponse;
import com.damvih.common.openapi.UnauthorizedErrorApiResponse;
import com.damvih.common.web.dto.ErrorResponseDto;
import com.damvih.storage.docs.common.InvalidPathErrorApiResponse;
import com.damvih.storage.docs.common.NotFoundErrorApiResponse;
import com.damvih.storage.dto.ResourceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation
@InvalidPathErrorApiResponse
@UnauthorizedErrorApiResponse
@NotFoundErrorApiResponse
@InternalServerErrorApiResponse
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Returns metadata of the resource after it has been moved.",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResourceResponseDto.class)
                )
        ),
        @ApiResponse(responseCode = "409", description = "Target resource already exists.",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "message": "Target resource already exists."
                                        }
                                        """
                        )
                )
        )
})
@SecurityRequirement(name = "authentication")
public @interface MovingResourceDocs {
}

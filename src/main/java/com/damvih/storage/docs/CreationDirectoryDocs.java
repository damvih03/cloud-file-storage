package com.damvih.storage.docs;

import com.damvih.common.openapi.InternalServerErrorApiResponse;
import com.damvih.common.openapi.UnauthorizedErrorApiResponse;
import com.damvih.common.web.dto.ErrorResponseDto;
import com.damvih.storage.docs.common.InvalidPathErrorApiResponse;
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
@UnauthorizedErrorApiResponse
@InvalidPathErrorApiResponse
@InternalServerErrorApiResponse
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResourceResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "path": "folder1/folder2/",
                                            "name": "folder3",
                                            "type": "DIRECTORY"
                                        }
                                        """
                        )

                )
        ),
        @ApiResponse(responseCode = "404", description = "Parent directory not found",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "message": "Parent directory not found."
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(responseCode = "409", description = "Already exists",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                            "message": "Directory already exists."
                                        }
                                        """
                        )
                )
        )
})
@SecurityRequirement(name = "authentication")
public @interface CreationDirectoryDocs {
}

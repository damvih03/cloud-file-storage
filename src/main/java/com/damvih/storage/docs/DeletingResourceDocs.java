package com.damvih.storage.docs;

import com.damvih.common.openapi.InternalServerErrorApiResponse;
import com.damvih.common.openapi.UnauthorizedErrorApiResponse;
import com.damvih.storage.docs.common.InvalidPathErrorApiResponse;
import com.damvih.storage.docs.common.NotFoundErrorApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
        @ApiResponse(responseCode = "204", description = "No content")
})
@SecurityRequirement(name = "authentication")
public @interface DeletingResourceDocs {
}

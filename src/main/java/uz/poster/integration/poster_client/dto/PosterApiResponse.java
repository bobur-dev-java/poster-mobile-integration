package uz.poster.integration.poster_client.dto;

public record PosterApiResponse<T>(
        T response,
        PosterApiError error
) {
    public record PosterApiError(Integer code, String message) {}

    public boolean hasError() {
        return error != null;
    }
}

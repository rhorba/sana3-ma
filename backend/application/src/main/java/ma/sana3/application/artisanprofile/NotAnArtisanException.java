package ma.sana3.application.artisanprofile;

public class NotAnArtisanException extends RuntimeException {

    public NotAnArtisanException() {
        super("Only users with the ARTISAN role may have an artisan profile");
    }
}

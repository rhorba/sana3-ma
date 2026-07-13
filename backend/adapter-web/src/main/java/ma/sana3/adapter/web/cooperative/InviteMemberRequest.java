package ma.sana3.adapter.web.cooperative;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteMemberRequest(@NotBlank @Email String email) {}

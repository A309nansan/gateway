package site.nansan.gateway.user;

public enum Role {
    USER, ADMIN, TEACHER;
    public static boolean isValidRole(String role) {
        try {
            Role.valueOf(role);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}



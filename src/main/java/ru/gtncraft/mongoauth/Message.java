package ru.gtncraft.mongoauth;

public enum Message {
    error_account_is_block {
        @Override
        public String toString() {
            return "&4Account is blocked.";
        }
    },
    error_account_is_auth {
        @Override
        public String toString() {
            return "&4You already authenticated.";
        }
    },
    error_account_not_registred {
        @Override
        public String toString() {
            return "&4Account not register.";
        }
    },
    error_account_online {
        @Override
        public String toString() {
            return "&4Player %s is online.";
        }
    },
    error_account_exists {
        @Override
        public String toString() {
            return "&4Player already register.";
        }
    },
    error_account_register_limit {
        @Override
        public String toString() {
            return "&4Registration limit per ip.";
        }
    },
    error_input_password {
        @Override
        public String toString() {
            return "&4You must supply password.";
        }
    },
    error_input_password_new {
        @Override
        public String toString() {
            return "&4You must supply new password.";
        }
    },
    error_input_passwords_equals {
        @Override
        public String toString() {
            return "&4New password equals old.";
        }
    },
    error_input_playername {
        @Override
        public String toString() {
            return "&4You must supply player.";
        }
    },
    error_input_password_missmach {
        @Override
        public String toString() {
            return "&4Invalid password.";
        }
    },
    error_input_invalid_login {
        @Override
        public String toString() {
            return "&4You name is not valid.";
        }
    },
    error_command_sender {
        @Override
        public String toString() {
            return "&4Only player can use this command.";
        }
    },
    error_emergency {
        @Override
        public String toString() {
            return "&4Emergency situation.";
        }
    },
    success_account_create {
        @Override
        public String toString() {
            return "&2Success register.";
        }
    },
    success_account_logout {
        @Override
        public String toString() {
            return "&Logout.";
        }
    },
    success_account_delete {
        @Override
        public String toString() {
            return "&2Account delete.";
        }
    },
    success_account_login {
        @Override
        public String toString() {
            return "&2You are log in.";
        }
    },
    success_change_password {
        @Override
        public String toString() {
            return "&2Password changed.";
        }
    },
    success_command_admin_changepassword {
        @Override
        public String toString() {
            return "&2Password changed for player %s.";
        }
    },
    success_command_admin_delete {
        @Override
        public String toString() {
            return "&2Account %s deleted.";
        }
    },
    success_command_admin_register {
        @Override
        public String toString() {
            return "&2Account %s created.";
        }
    },
    command_register_hint {
        @Override
        public String toString() {
            return "Register command &a/register password";
        }
    },
    command_login_hint {
        @Override
        public String toString() {
            return "Login command &a/register password";
        }
    }
}

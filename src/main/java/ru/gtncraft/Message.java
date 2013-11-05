package ru.gtncraft;

import org.bukkit.ChatColor;

final public class Message {
    public static final String WHITELIST = ChatColor.RED + "Вас нет в WHITELIST";
    public static final String PLAYER_IS_LOGGED = ChatColor.GREEN + "Вы уже авторизованы";
    public static final String PLAYER_NOT_REGISTER = ChatColor.RED + "Игрок с таким именем не зарегистрирован";
    public static final String LOGOUT_SUCCESS = ChatColor.GREEN + "Игровая сессия завершена";
    public static final String REGISTER_SUCCESS = ChatColor.GREEN + "Регистрация завершина, хорошего крафта!";
    public static final String REGISTER_COMMAND_HINT = ChatColor.GREEN + "Зарегистрируйтесь с помощью команды " + ChatColor.DARK_PURPLE + "/register пароль";
    public static final String REGISTER_LIMIT_REACHED = ChatColor.RED + "Достигнут предел регистраций с данного IP адреса";
    public static final String PASSWORD_WRONG = ChatColor.RED + "Неправильный пароль";
    public static final String PASSWORD_MISSING = ChatColor.RED + "Введите пароль";
    public static final String LOGIN_NOT_VALID = ChatColor.RED + "Имя игрока содержит неправильные символы";
    public static final String PLAYER_ALREADY_REGISTERED = ChatColor.RED + "Игрок с таким именем уже зарегистрирован";
    public static final String UNREGISTER_SUCCESS = ChatColor.GREEN + "Игровой аккаунт удален";
    public static final String UNREGISTER_COMMAND_HINT = ChatColor.GREEN +  "Удалить игровой аккунт можно коммандой " + ChatColor.DARK_PURPLE + "/unregister пароль";
    public static final String LOGIN_COMMAND_HINT = ChatColor.GREEN + "Авторизируйтесь с помощью команды " + ChatColor.DARK_PURPLE + "/login пароль";
    public static final String LOGIN_SUCCESS = ChatColor.GREEN + "Вы успешно авторизованы";
    public static final String CPW_COMMAND_HINT = ChatColor.GREEN + "Сменить пароль можно коммандой " + ChatColor.DARK_PURPLE + "/cpw пароль новый_пароль";
    public static final String CPW_SUCCESS = ChatColor.GREEN + "Ваш пароль был изменен";
    public static final String SENDER_NOT_VALID = ChatColor.RED + "SimpleCommand sender not valid";
    public static final String PERMISSION_FORBIDDEN = ChatColor.RED + "У вас недостаточно прав для выполнения этой комманды";
    public static final String PLAYER_ALREADY_ONLINE = "Игрок с ником %s сейчас находится в игре.";
    public static final String REGISTER_OR_LOGIN = ChatColor.DARK_RED + "Пожалуйста зарегистрируйтесь /register или авторизуйтесь /login";

    public static final String ADMIN_MISSING_PLAYERNAME = ChatColor.RED + "Не указано имя игрока";
    public static final String ADMIN_MISSING_PLAYERNAME_OR_PASSWORD = ChatColor.RED + "Не указано имя игрока или пароль";
    public static final String ADMIN_SUCCESS_CHANGE_PASSWORD = ChatColor.GREEN + "Пароль для игрока %s был изменён.";
    public static final String ADMIN_SUCCESS_PLAYER_UREGISTRED = ChatColor.GREEN + "Аккаунт игрока %s был удален.";
    public static final String ADMIN_SUCCESS_REGISTER_PLAYER = ChatColor.GREEN + "Аккаунт игрока %s зарегистрирован.";
}

package datasource.db.sqlite;

import datasource.db.sqlite.user.InsertUserStatementPrepare;
import datasource.db.sqlite.user.SetIpStatementPrepare;
import datasource.db.sqlite.user.UserDataExtractor;
import datasource.db.sqlite.utils.SetIdStatementDelegate;
import datasource.db.sqlite.utils.StatementHelper;
import datasource.model.User;

import java.sql.Connection;

public class UserControl extends AbstractEntityContainer {
    private final String SELECT_USER_BY_IP_TEMPLATE = "select id, dns, ip, isIpv4 from user where ip = ?";
    private final String SELECT_USER_BY_ID_TEMPLATE = "select id, dns, ip, isIpv4 from user where id = ?";
    private final String ADD_USER_TEMPLATE = "insert into user(dns, ip, isIpv4) values (?, ?, ?)";
    private final String DELETE_USER_BY_ID_TEMPLATE = "delete from user where id = ?";


    public UserControl(Connection connection) {
        super(connection);
    }

    public User getUser(int userId)
    {
        return StatementHelper.executeWithResult(new SetIdStatementDelegate(userId), connection,
                SELECT_USER_BY_ID_TEMPLATE, new UserDataExtractor());
    }

    public User getUser(String ip)
    {
        return StatementHelper.executeWithResult(new SetIpStatementPrepare(ip), connection,
                SELECT_USER_BY_IP_TEMPLATE, new UserDataExtractor());
    }

    public boolean addUser(User user)
    {
        return StatementHelper.executeWithoutResult(new InsertUserStatementPrepare(user), connection,
                ADD_USER_TEMPLATE);
    }

    public boolean deleteUser(int id) {
        return StatementHelper.executeWithoutResult(new SetIdStatementDelegate(id), connection,
                DELETE_USER_BY_ID_TEMPLATE);
    }
}

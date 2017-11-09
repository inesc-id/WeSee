package datasource.db.sqlite.user;

import datasource.db.sqlite.utils.IDataExtractor;
import datasource.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataExtractor implements IDataExtractor<User> {
    @Override
    public User readDataFromStatement(ResultSet resultSet) throws SQLException {
        if (resultSet.next())
        {
            User user = new User();
            user.id = resultSet.getInt("id");
            user.ip = resultSet.getString("ip");
            user.isIpv4 = resultSet.getInt("isIpv4") == 1;
            user.dns = resultSet.getString("dns");
            return user;
        }
        return null;


    }
}

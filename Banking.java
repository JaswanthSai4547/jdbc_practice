import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Banking {
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int id ;

    Banking() {
        try {
                //Class.forName("com.mysql.jdbc.Driver");
                //The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "Test@1234");
            System.out.println("connection established");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void create_account(){
        try {
            System.out.println("enter name :");
            String name = br.readLine();
            System.out.println("enter phone number");
            int phno = Integer.parseInt(br.readLine());
            System.out.println("enter address :");
            String address = br.readLine();
            System.out.println("enter a/c number:");
            int acno = Integer.parseInt(br.readLine());
            System.out.println("enter email:");
            String email = br.readLine();

            ps = con.prepareStatement("insert into bank(name,ph_no,address,ac_no,email) values (?,?,?,?,?)");
            ps.setString(1,name);
            ps.setInt(2,phno);
            ps.setString(3,address);
            ps.setInt(4,acno);
            ps.setString(5,email);
            int count=ps.executeUpdate();
            if(count==1) {
                System.out.println(count + " row inserted");
            }
        }catch (IOException e){
            System.out.println("IO exception");
            System.out.println(e);
        }
        catch(NumberFormatException e){
            System.out.println("invalid input in insert method");
        }
        catch (SQLException e){
            System.out.println("a/c no already exist");
        }
    }
    public void display() throws Exception{
        //one way to display in table format using formatter class
//        Formatter fmt = new Formatter();
//        fmt.format("%-15s %-15s %-15s %-15s %-15s\n","name","ph_no","address","ac_no","email");
//        ps = con.prepareStatement("select * from bank");
//        rs = ps.executeQuery();
//        while (rs.next()){
//            fmt.format("%-15s %-15s %-15s %-15s %-15s\n",rs.getString(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getString(5));
//        }
//        System.out.println(fmt);

        //another way by using printf statement spaces

        ps = con.prepareStatement("select * from bank");
        rs = ps.executeQuery();
        System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n","name","ph_no","address","ac_no","email","balance");
        while (rs.next()) {
            System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n",rs.getString(1), rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getString(5),rs.getDouble(6));
        }

    }
    public void search(){
        try {
            System.out.println("enter a/c no:");
            int acno = Integer.parseInt(br.readLine());
            ps = con.prepareStatement("select * from bank where ac_no=?");
            ps.setInt(1,acno);
            rs = ps.executeQuery();
            if(rs.next()) {
                System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n", "name", "ph_no", "address", "ac_no", "email","balance");
                System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n",rs.getString(1), rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getString(5),rs.getDouble(6));

                System.out.println("transaction history ");
                ps = con.prepareStatement("select * from Transaction where ac_no=?");
                ps.setInt(1,acno);
                rs = ps.executeQuery();
                System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n", "ac_no", "tid", "date", "time", "t_amount","av_balance");
                while(rs.next()){
                    System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n",rs.getInt(1),rs.getString(2),rs.getDate(3),rs.getTime(4),rs.getString(5),rs.getDouble(6));
                }
            }else {
                System.out.println("record/ac_no not found.");
            }
        }catch (Exception e){
            System.out.println("invalid input in search");
        }
    }
    public void credit(){
        try {
            System.out.println("enter account number:");
            int acno = Integer.parseInt(br.readLine());
            System.out.println("enter amount :");
            double amount = Double.parseDouble(br.readLine());
            double d=0.0;
            ps = con.prepareStatement("update bank set balance=balance+? where ac_no=?");
            ps.setDouble(1,amount);
            ps.setInt(2,acno);
            if(ps.executeUpdate()==1){
                System.out.println("amount credited successfully");
                System.out.print("available balance :");
                ps = con.prepareStatement("select balance from bank where ac_no = ?");
                ps.setInt(1,acno);
                rs = ps.executeQuery();
                if(rs.next()) {
                    d=rs.getDouble(1);
                    System.out.println(d);
                }

                java.util.Date today = new java.util.Date();
                ps = con.prepareStatement("select @tid");
                rs = ps.executeQuery();
                rs.next();
                id = rs.getInt(1)+1;
                System.out.println("--"+id);

                ps = con.prepareStatement("set @tid=?");
                ps.setInt(1,id);
                ps.executeUpdate();


//                ps = con.prepareStatement("select @tid");
//                rs = ps.executeQuery();
//                rs.next();
//                System.out.println("updated"+rs.getInt(1));

                ps = con.prepareStatement("insert into Transaction values(?,?,?,?,?,?)");
                ps.setInt(1,acno);
                ps.setString(2,'C'+Integer.toString(id));
                ps.setDate(3,new java.sql.Date(today.getTime()));
                ps.setTime(4,new java.sql.Time(today.getTime()));
                ps.setString(5,'+'+Double.toString(amount));
                ps.setDouble(6,d);
                ps.executeUpdate();
            }else {
                System.out.println("record/ac_no not found");
            }
        }catch (Exception e){
            System.out.println("invalid input in credit");
            System.out.println(e);
        }
    }
    public void debit(){
        try{
            System.out.println("enter account number:");
            int acno = Integer.parseInt(br.readLine());
            System.out.println("enter amount:");
            double amount = Double.parseDouble(br.readLine());
            double d=0.0;
            ps = con.prepareStatement("select balance from bank where ac_no=?");
            ps.setDouble(1,acno);
            rs = ps.executeQuery();
            if(rs.next())
            {
                ps = con.prepareStatement("select balance from bank where balance>=?");
                ps.setDouble(1,amount);
                rs = ps.executeQuery();
                if(rs.next())
                {
                    ps = con.prepareStatement("update bank set balance=balance-? where ac_no=? and balance>=?");
                    ps.setDouble(1, amount);
                    ps.setInt(2, acno);
                    ps.setDouble(3, amount);
                    if (ps.executeUpdate() == 1) {
                        System.out.println("amount debited successfully");
                        System.out.print("available balance :");
                        ps = con.prepareStatement("select balance from bank where ac_no = ?");
                        ps.setInt(1, acno);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            d=rs.getDouble(1);
                            System.out.println(d);
                        }
                        java.util.Date today = new java.util.Date();
                        ps = con.prepareStatement("select @tid");
                        rs = ps.executeQuery();
                        rs.next();
                        id = rs.getInt(1)+1;
                        ps = con.prepareStatement("set @tid=?");
                        ps.setInt(1,id);
                        ps.executeUpdate();
                        ps = con.prepareStatement("insert into Transaction values(?,?,?,?,?,?)");
                        ps.setInt(1,acno);
                        ps.setString(2,'D'+Integer.toString(id));
                        ps.setDate(3,new java.sql.Date(today.getTime()));
                        ps.setTime(4,new java.sql.Time(today.getTime()));
                        ps.setString(5,'-'+Double.toString(amount));
                        ps.setDouble(6,d);
                        ps.executeUpdate();
                    }
                }else{
                    System.out.println("insufficient balance");
                }
            }
            else {
                System.out.println("record/ac_no not found");
            }
        }catch (Exception e){
            System.out.println("invalid input in debit");
            System.out.println(e);
        }
    }
    public void delete(){
        try {
            System.out.println("enter a/c no:");
            int acno = Integer.parseInt(br.readLine());
            ps = con.prepareStatement("delete from bank where ac_no=?");
            ps.setInt(1,acno);
            ps.executeUpdate();
            ps = con.prepareStatement("delete from Transaction where ac_no=?");
            ps.setInt(1,acno);
            ps.executeUpdate();
            System.out.println("account deleted successfully");
        }catch (Exception e){
            System.out.println("invalid input in delete");
            System.out.println(e);
        }
    }
    public void close() throws Exception{
        con.close();
        System.out.println("connection closed");
    }
}

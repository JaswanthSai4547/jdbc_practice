import java.io.BufferedReader;
import java.io.InputStreamReader;
public class Main
{
    public static void main(String[] args) {
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        Banking b = new Banking();
        while (true){
            try{
                System.out.println("1.create account");
                System.out.println("2.display all");
                System.out.println("3.search");
                System.out.println("4.credit");
                System.out.println("5.debit");
                System.out.println("6.delete");
                System.out.println("0.exit");
                int choice = Integer.parseInt(br1.readLine());
                switch (choice){
                    case 1:
                        b.create_account();
                        break;
                    case 2:
                        b.display();
                        break;
                    case 3:
                        b.search();
                        break;
                    case 4:
                        b.credit();
                        break;
                    case 5:
                        b.debit();
                        break;
                    case 6:
                        b.delete();
                        break;
                    case 0:
                        b.close();
                        System.exit(0);
                }
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
package nn.EmpMgt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuBased {

    static Scanner inp = new Scanner(System.in);
    static String pattern = "dd/MM/yyyy";
    static SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    static String disLast = null;
    
    public static void newDatabase() throws InterruptedException, IOException
    {
        File file;
        //System.out.println("\r");
        System.out.print("New database name: ");
        String filename = inp.nextLine();
        
        while (true) { 
            file = new File(filename + ".dat");
            if(file.exists())
            {
                //System.out.println("\r");
                System.out.println("There is already database name " + filename);
                System.out.print("Re-enter name: ");
                filename = inp.nextLine();
            }
            else
                break;
        }
        var fos = new FileOutputStream(file);
        var dos = new DataOutputStream(fos);
        
        //System.out.println("\r");
        System.out.println("New Database made as: " + filename);
        fos.close();
        dos.close();
        Thread.sleep(2000);
    } 

    public static void OpenDatabase() throws InterruptedException, IOException, ParseException
    {
        File file;
        //System.out.println("\r");
        System.out.print("Open database name: ");
        String filename = inp.nextLine();
        while (true) { 
            file = new File(filename + ".dat");
            if(!file.exists())
            {
                //System.out.println("\r");
                System.out.println("There no database name " + filename);
                System.out.print("Re-enter name: ");
                filename = inp.nextLine();
            }
            else
                break;
        }
        //System.out.println("\r");
        System.out.println("Opened Database: " + filename);
        
        var fos = new FileOutputStream(file, true);
        var dos = new DataOutputStream(fos);

        Thread.sleep(1000);
        

        while (true) { 
            //System.out.println("\r");
            System.out.println("""
                                1. Add Data (working)\n
                                2. Edit Data\n
                                3. Delete Data\n
                                4. SearchById (partial working)\n
                                5. DisplayLast (working)\n
                                6. DisplayAll (working)\n
                                7. Close Database (working)
                                """);
            System.out.print("> ");
            int input = inp.nextInt();
            inp.nextLine(); 
            
            if(input == 1)
            {
                addData(dos);
            }
            else if(input == 2)
            {

            }
            else if(input == 3)
            {
                
            }else if(input == 4)
            {
                //System.out.println("\r");
                System.out.print("Enter id to search: ");
                int id = inp.nextInt();
                findDataById(filename+".dat", id);
            }
            else if(input == 5)
            {
                showLastData();
            }
            else if(input == 6)
            {
                displayAllData(file);
            }
            else if(input == 7)
            {
                break;
            }
            else
                System.out.println("Invalid Input");

        }
    }

    private static void findDataById(String filename, int findId) throws FileNotFoundException, IOException, ParseException, InterruptedException
    {
        ArrayList<Integer> al = new ArrayList<>();
        int id;
        String name;
        String dt;
        float salary;
        
        var fis = new FileInputStream(filename);
        var dis = new DataInputStream(fis);
        try
           {
               while (true) {
                   id = dis.readInt();
                   dis.readUTF();
                   dis.readUTF();
                   dis.readFloat();
                    System.out.println(id);
                   al.add(id);
               }
           }
            catch (IOException e) { System.out.println("reached EOF");}

        fis = new FileInputStream(filename);
        dis = new DataInputStream(fis);

        //System.out.println("\r");


        if(al.contains(findId))
            {
                int pos = al.indexOf(findId);
                for(int i = 0 ; i < pos ; i++)
                {
                    System.out.println(i);
                    dis.readInt();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readFloat();
                    if(i+1 == pos)
                    {
                        id = dis.readInt();
                        name = dis.readUTF();
                        dt = dis.readUTF();
                        salary = dis.readFloat();

                        System.out.println("Found Data at index " + pos + "\nID: " + id + "\nName: " + name + "\nDOB: " + sdf.parse(dt) + "\nSalary: " + salary);

                        Thread.sleep(3000);
                    }
                    
                }
            }
            else
                System.out.println("Data with ID: " + findId + " is not found");

            fis.close();
            dis.close();
    }

    public static void displayAllData(File file) throws InterruptedException
    {
        try (
                var fis = new FileInputStream(file);
                var dis = new DataInputStream(fis);
            ) 
            {

            //System.out.println("\r");

                while (true) {
                    int id = dis.readInt();
                    String name = dis.readUTF();
                    String dt = dis.readUTF();
                    float salary = dis.readFloat();

                    System.out.println("Emp [id=" + id + ", name=" + name + ", dob=" + dt + ", salary=" + salary + "]");
                }
            } catch (IOException e) {
                System.out.println("Read all");
            } 

        Thread.sleep(3000);
    }

    public static void addData(DataOutputStream os) throws InterruptedException, IOException
    {
        //System.out.println("\r");
        System.out.println("Adding New Data");
        System.out.print("Enter id: ");
        int id = inp.nextInt();
        inp.nextLine();
        System.out.print("Enter name: ");
        String name = inp.nextLine();
        System.out.print("Enter dob [dd/mm/yyyy]: ");
        String dob = inp.nextLine();
        while (true) { 
            try
            {
                sdf.parse(dob);
                break;
            } 
            catch (ParseException e) {
                System.out.println("Wrong date format use dd/mm/yyyy");
                System.out.print("Re-enter dob: ");
                dob = inp.nextLine();
            }
        }
        System.out.print("Enter salary: ");
        float salary = inp.nextFloat();

        os.writeInt(id);
        os.writeUTF(name);
        os.writeUTF(dob);
        os.writeFloat(salary);
        
        disLast = "Emp [id=" + id + ", name=" + name + ", dob=" + dob + ", salary=" + salary + "]";
        System.out.println("Data with name " + name + " is added to the Database");
        Thread.sleep(2000);

    }

    public static void main(String[] args) throws InterruptedException, IOException, ParseException {

        while (true) { 
            // System.out.println("""
            //                    1. New\n
            //                    2. Open\n
            //                    3. Save\n
            //                    4. SaveAs\n
            //                    5. Add\n
            //                    6. Edit\n
            //                    7. Delete\n
            //                    8. SearchById\n
            //                    9. DisplayLast\n
            //                    10. DisplayAll
            //                    """);
            // //System.out.println("\r");
            System.out.println("""
                               1. New Database (working)\n
                               2. Open Database (working)\n
                               3. Delete Database (working)\n
                               4. Rename Database (working)\n
                               5. Quit (working)\n
                               """);
            System.out.print("> ");
            int input = inp.nextInt();
            inp.nextLine();

            if(input == 1)
            {
                newDatabase();
            }
            else if(input == 2)
            {
                OpenDatabase();
            }
            else if(input == 3)
            {
                deleteDatabase();
            }
            else if(input == 4)
            {
                renameDatabase();
            }
            else if(input == 5)
            {
                break;
            }
            else
                System.out.println("Invalid Input");
        }
        inp.close();
        System.out.println("Goodbye");
    }

    public static void deleteDatabase() throws InterruptedException
    {
        File file;
        //System.out.println("\r");
        System.out.print("Select database name: ");
        String filename = inp.nextLine();
        while (true) { 
            file = new File(filename + ".dat");
            if(!file.exists())
            {
                //System.out.println("\r");
                System.out.println("There no database name " + filename);
                System.out.print("Re-enter name: ");
                filename = inp.nextLine();
            }
            else
                break;
        }
        //System.out.println("\r");
        System.out.print("Do really want to delete Database" + filename + "(y/n): " );
        char flag = inp.next().charAt(0);

        if (flag == 'y') {
            try
            {
                if(file.delete())
                    System.out.println("Deleted Database " + filename + "Succesfully");
            }
            catch(Exception ex)
            {
                System.out.println("Error occur while deleting Database " + filename);
                System.out.println(ex.getStackTrace());
            }

        }
        else
            System.out.println("Cancel by user. Going back");

        Thread.sleep(2000);
    }

    public static void showLastData()
    {
        if(disLast != null)
            System.out.println(disLast);
        else
            System.out.println("No last data is manipulated");
    }

    public static void renameDatabase() throws InterruptedException
    {
        File file;
        //System.out.println("\r");
        System.out.print("Select database name: ");
        String filename = inp.nextLine();
        while (true) { 
            file = new File(filename + ".dat");
            if(!file.exists())
            {
                //System.out.println("\r");
                System.out.println("There no database name " + filename);
                System.out.print("Re-enter name: ");
                filename = inp.nextLine();
            }
            else
                break;
        }
        //System.out.println("\r");
        System.out.print("Enter new name for Database " + filename + ": " );
        String newFilename = inp.nextLine();
        File nFile = new File(newFilename + ".dat");
        try
        {
            file.renameTo(nFile);
            System.out.println("Succesfully renamed " + file.getName() + " to " + nFile.getName());
        }
        catch(NullPointerException ex)
        {
            System.out.println("Error Occur");
            System.out.println(ex.getStackTrace());
        }
        Thread.sleep(2000);
    }
}
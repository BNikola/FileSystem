import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        FileSystem.disc.formatDisc();
        FileSystem fs = new FileSystem();

        Scanner scanner = new Scanner(System.in);
        String command = "";
        do {
            command = scanner.nextLine();
            if (command.startsWith("ls")) {
                String [] arr = command.split(" ");
                if (arr.length == 1) {
                    fs.ls();
                } else {
                    fs.ls(arr[1]);
                }
            } else if (command.startsWith("mkdir")) {
                String[] arr = command.split(" ");
                if (arr.length == 2) {
                    fs.mkdir(arr[1]);
                } else {
                    System.out.println("Not enough parameters form mkdir");
                }
            } else if (command.startsWith("create")) {
                String[] arr = command.split(" ");
                if (arr.length == 2) {
                    fs.create(arr[1]);
                } else {
                    System.out.println("Not enough parameters form create");
                }
            } else if (command.startsWith("rm")) {
                String[] arr = command.split(" ");
                if (arr.length == 2) {
                    fs.rm(arr[1]);
                } else if (arr.length == 3) {
                    fs.rm(arr[1], arr[2]);
                } else {
                    System.out.println("Not enough parameters form rm");
                }
            } else if (command.startsWith("cp")) {
                String[] arr = command.split(" ");
                if (arr.length == 3) {
                    fs.cp(arr[1], arr[2]);
                } else {
                    System.out.println("Not enough parameters form cp");
                }
            } else if (command.startsWith("mv")) {
                String[] arr = command.split(" ");
                if (arr.length == 3) {
                    fs.mv(arr[1], arr[2]);
                } else {
                    System.out.println("Not enough parameters form mv");
                }
            } else if (command.startsWith("rename")) {
                String[] arr = command.split(" ");
                if (arr.length == 3) {
                    fs.rename(arr[1], arr[2]);
                } else {
                    System.out.println("Not enough parameters form rename");
                }
            } else if (command.startsWith("cat")) {
                String[] arr = command.split(" ");
                if (arr.length == 2) {
                    fs.cat(arr[1]);
                }
            } else if (command.startsWith("echo")) {
                String[] arr = command.split(" ");
                if (arr.length == 3) {
                    fs.echo(arr[1], arr[2]);
                }
            } else if (command.startsWith("put")) {
                String[] arr = command.split(" ");
                if (arr.length == 3) {
                    fs.put(arr[1], arr[2]);
                }
            } else if (command.startsWith("get")) {
                String[] arr = command.split(" ");
                if (arr.length == 3) {
                    fs.get(arr[1], arr[2]);
                }
            } else {
                System.out.println("Unknown command");
            }
        } while (!command.equals("EXIT"));
    }
}

package Grapie.Functions;

import Grapie.Exceptions.GrapieExceptions;
import Grapie.Tasks.Deadline;
import Grapie.Tasks.Event;
import Grapie.Tasks.Task;
import Grapie.Tasks.Todo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Storage {
    private String filePath;
    List<Task> storingList;

    /**
     * Constructor for Grapie.Functions.Storage class.
     *
     * @param filePath The filepath to get the Hard Disk data from.
     * @throws IOException Throws exception.
     */
    public Storage(String filePath, List<Task> storingList) throws IOException {
        // deals with loading tasks from the file and saving tasks in the dukeStorage.txt file.
        this.filePath = filePath;
        File file = new File(filePath);
        file.createNewFile();
        this.storingList = storingList;
    }

    /**
     * Load hard disk information into an ArrayList for Grapie.Command.TaskList class to use.
     *
     * @return Returns an ArrayList of Grapie.Tasks.Task, loaded from the Hard Disk.
     * @throws FileNotFoundException Throws exception.
     * @throws GrapieExceptions Throws exception.
     */
    public List<Task> load() throws FileNotFoundException, GrapieExceptions {

        File myObj = new File(filePath);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();

            /*
            T | 1 | read book
            D | 0 | return book | June 6th
            E | 0 | project meeting | Aug 6th 2-4pm
            T | 1 | join sports club
            */

            String[] dataSplited = data.split("\\|");

            //trim off empty spaces at front and back of string
            for (int i = 0; i < dataSplited.length; i++) {
                dataSplited[i] = dataSplited[i].trim();
            }

            if (dataSplited.length == 3) {
                //is a todo
                Task task = new Todo(dataSplited[2]);
                if (dataSplited[1].equals("O")) {
                    task.isDone = true;
                }
                storingList.add(task);

            } else if (dataSplited.length == 4) {
                //is a event or deadline
                if (dataSplited[0].equals("E")) {
                    //event
                    Event task = new Event(dataSplited[2], dataSplited[3]);
                    if (dataSplited[1].equals("O")) {
                        task.isDone = true;
                    }
                    storingList.add(task);

                } else {
                    //deadline
                    Deadline task = new Deadline(dataSplited[2], dataSplited[3]);
                    if (dataSplited[1].equals("O")) {
                        task.isDone = true;
                    }
                    storingList.add(task);
                }
            }
        }
        return storingList; //return the filled list
    }

    /**
     * Convert task into correct format, and store in dukeStorage.txt file.
     *
     * @param task The task to be converted into the new format.
     * @param type Grapie.Tasks.Todo, Grapie.Tasks.Event or Grapie.Tasks.Deadline.
     * @param time The date and time for the Tasks.
     * @throws IOException Throws exception.
     */
    public void convertAndStore(Task task, String type, String time) throws IOException {
        String doneOrNotDone = "";
        if (task.isDone) {
            doneOrNotDone += "O";
        } else {
            doneOrNotDone += "X";
        }

        String newDescription = "";
        if (type.equals("T")) {
            //todo
            newDescription += type + " | " + doneOrNotDone + " | " + task.description;
        } else {
            //event & deadline
            newDescription += type + " | " + doneOrNotDone + " | " + task.description + " | " + time;
        }

        File file = new File(filePath);
        FileWriter fr = new FileWriter(file, true);

        if (file.length() == 0) {
            fr.write(newDescription);
        } else {
            fr.write("\n" + newDescription);
        }

        fr.close();
    }

    /**
     * Modify the done value in the dukeStorage.txt file.
     * Change not done (X) into done (O)
     *
     * @param lineNumber The line number to be deleted from hard disk.
     * @throws IOException Throws exception.
     */
    public void replaceLineFromHardDisk(int lineNumber) throws IOException {
        File myObj = new File(filePath);
        Scanner myReader = new Scanner(myObj);

        String newData = "";
        int counter = 1;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            //System.out.println(data);

            if (counter == lineNumber) {
                data = data.substring(0, 4) + "O" + data.substring(5, data.length());
            }

            if (counter == 1) {
                newData += data;
            } else {
                newData += "\n" + data;
            }

            counter++;
        }

        FileOutputStream fileOut = new FileOutputStream(filePath);
        fileOut.write(newData.getBytes());
        fileOut.close();
    }

    /**
     * Delete the corresponding line from dukeStorage.txt file according to the task deleted.
     *
     * @param lineNumber The line number to be deleted from hard disk.
     * @throws IOException Throws exception.
     */
    public void deleteLineFromHardDisk(int lineNumber) throws IOException {
        File myObj = new File(filePath);
        Scanner myReader = new Scanner(myObj);

        String newData = "";
        boolean isFirstLineDone = false;
        int counter = 1;

        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();

            if (counter != lineNumber) {
                if (!isFirstLineDone) {
                    newData += data;
                    isFirstLineDone = true;
                } else {
                    newData += "\n" + data;
                }
            }
            counter++;
        }

        FileOutputStream fileOut = new FileOutputStream(filePath);
        fileOut.write(newData.getBytes());
        fileOut.close();
    }
}

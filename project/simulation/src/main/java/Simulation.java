import master.Master;

import java.util.*;

public class Simulation {

    public static void main(String[] args) {

        final Cli cli = new Cli();

        cli.start();

    }

    private static class Cli {

        private Scanner scanner;

        private List<Master> masters = new ArrayList<>();

        public Cli() {
            this.scanner = new Scanner(System.in);
        }

        public void start() {
            this.build(new InitialPage());
        }

        public Scanner getScanner() {
            return scanner;
        }

        public void build(final Page page) {
            page.show(this);
        }

        public void addMaster(final Master master) {
            masters.add(master);
        }

        public List<Master> getMasters() {
            return masters;
        }

    }

    private interface Page {
        void show(final Cli cli);
    }

    private static class InitialPage implements Page {

        @Override
        public void show(final Cli cli) {
            String content = "Welcome to Simulation (Part 1) app.\n" +
               "This is a command line interface that you can interact with. The interactions are the following:\n" +
               "1 - Build a new master for simulation\n" +
               "2 - Start a simulation\n" +
               "3 - Debug mode\n" +
               "You can, at any time, go back to this menu by pressing the key 'h'\n";
            System.out.print(content);

            Page nextPage = null;

            final Scanner scanner = cli.getScanner();

            while(nextPage == null) {
                String input = scanner.next();

                switch (input) {
                    case "1":
                        nextPage = new MasterBuilderPage(Master.MasterBuilder.create());
                        break;
                    case "2":
                        nextPage = new StartSimulationPage();
                        break;
                    case "3":
                        nextPage = new DebugModePage();
                        break;
                    case "h":
                        nextPage = new InitialPage();
                        break;
                    default:
                        System.out.println("\nInvalid option");
                }
            }

            cli.build(nextPage);
        }

    }

    private static class MasterBuilderPage implements Page {

        private final Master.MasterBuilder builder;

        public MasterBuilderPage(final Master.MasterBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void show(final Cli cli) {
            String content = String.format(
                    "Current Master Build: %s\n" +
                    "1 - Add new slave\n" +
                    "2 - Add new worker\n" +
                    "3 - Use Performance Index Slave Scheduler\n" +
                    "4 - Use Lock-Based Slave Handler\n" +
                    "5 - Use Lock-Free Slave Handler\n" +
                    "B - Build Master\n",
                    builder.toString()
            );
            System.out.print(content);

            Page nextPage = null;

            final Scanner scanner = cli.getScanner();

            while(nextPage == null) {
                String input = scanner.next();

                switch (input) {
                    case "1":
                        nextPage = new SlaveBuilderPage(builder);
                        break;
                    case "2":
                        builder.withWorker();
                        nextPage = new MasterBuilderPage(builder);
                        break;
                    case "3":
                        builder.withPerformanceIndexScheduler();
                        nextPage = new MasterBuilderPage(builder);
                        break;
                    case "4":
                        builder.withLockBasedSlaveHandler();
                        nextPage = new MasterBuilderPage(builder);
                        break;
                    case "5":
                        builder.withLockFreeSlaveHandler();
                        nextPage = new MasterBuilderPage(builder);
                        break;
                    case "B":
                        final Master master = builder.build();
                        cli.addMaster(master);
                        nextPage = new InitialPage();
                        break;
                    case "h":
                        nextPage = new InitialPage();
                        break;
                    default:
                        System.out.println("\nInvalid option");
                }
            }

            cli.build(nextPage);
        }

    }

    private static class SlaveBuilderPage implements Page {

        private final Master.MasterBuilder builder;

        public SlaveBuilderPage(final Master.MasterBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void show(final Cli cli) {
            String content = "Performance Index of Slave ?\n";

            System.out.print(content);

            Page nextPage = null;

            final Scanner scanner = cli.getScanner();

            while(nextPage == null) {
                String input = scanner.next();

                Integer chosenNumber = -1;

                try{
                    chosenNumber = Integer.parseInt(input);
                }catch (Exception e) {

                }

                if(chosenNumber != -1) {
                    builder.withSlave(chosenNumber);

                    nextPage = new MasterBuilderPage(builder);
                } else if(input.equals("h")) {
                    nextPage = new InitialPage();
                } else {
                    System.out.println("\nInvalid Option");
                }
            }

            cli.build(nextPage);
        }

    }

    private static class StartSimulationPage implements Page {

        @Override
        public void show(final Cli cli) {

            String content;

            Page nextPage = null;

            final Scanner scanner = cli.getScanner();

            final List<Master> masters = cli.getMasters();

            if(cli.getMasters().isEmpty()) {
                content = "You haven't build no masters.\n";
            } else {
                StringBuilder builder = new StringBuilder();

                builder.append("Choose a master:\n");

                for(int i = 0; i < masters.size(); i++) {
                    builder.append(String.format("%c - %s\n", i + 1,masters.get(i)));
                }

                content = builder.toString();
            }

            System.out.print(content);

            while(nextPage == null) {
                String input = scanner.next();

                Integer chosenNumber = -1;

                try{
                    chosenNumber = Integer.parseInt(input);
                }catch (Exception e) {

                }

                if(chosenNumber != -1){
                    if(chosenNumber <= masters.size()) {
                        final Master selectedMaster = masters.get(chosenNumber - 1);
                        nextPage = new SimulationPage(selectedMaster);
                    } else {
                        System.out.println("\nInvalid option");
                    }
                } else if(input.equals("h")) {
                    nextPage = new InitialPage();
                } else {
                    System.out.println("\nInvalid Option");
                }
            }

            cli.build(nextPage);
        }

    }

    private static class SimulationPage implements Page {

        private final Master master;

        public SimulationPage(final Master master) {
            this.master = master;
        }

        @Override
        public void show(final Cli cli) {

            Page nextPage = null;

            final Scanner scanner = cli.getScanner();

            String content = "Welcome to simulation mode. The available options are the following:\n" +
                    "1 - Request slaves to calculate sum of random numbers\n" +
                    "2 - Request slaves to calculate multiplication of random numbers\n" +
                    "3 - Request slaves to report their performance index\n";

            System.out.print(content);

            while(nextPage == null) {
                String input = scanner.next();

                switch (input) {
                    case "1":
                        master.requestSumOfNumbers(randomSequenceOfNumbers());
                        break;
                    case "2":
                        master.requestMultiplicationOfNumbers(randomSequenceOfNumbers());
                        break;
                    case "3":
                        master.requestSlavesPerformanceIndex();
                        break;
                    case "h":
                        nextPage = new InitialPage();
                        break;
                    default:
                        System.out.println("\nInvalid option");
                }
            }

            cli.build(nextPage);
        }

        private List<Integer> randomSequenceOfNumbers() {

            final List<Integer> toCalculate = new ArrayList<>();

            final int size = new Random().nextInt(50000);

            for(int j = 0; j < size; j++) {

                toCalculate.add(j);

            }

            return toCalculate;

        }

    }

    private static class DebugModePage implements Page {

        @Override
        public void show(final Cli cli) {
            String content = "Welcome to debug mode. The available options are the following:\n" +
                    "1 - Print all slaves performance index\n" +
                    "2 - Print all slaves availability\n";

            System.out.print(content);

            Page nextPage = null;

            final Scanner scanner = cli.getScanner();

            while(nextPage == null) {
                String input = scanner.next();

                switch (input) {
                    case "1":
                        break;
                    case "2":
                        break;
                    case "h":
                        nextPage = new InitialPage();
                        break;
                    default:
                        System.out.println("\nInvalid option");
                }
            }

            cli.build(nextPage);
        }

    }

    @FunctionalInterface
    private interface Widget {

        String build(Map.Entry<Character, Widget>... actions);

    }

    @FunctionalInterface
    private interface asd {
        void apply(String a, String b);
    }

}

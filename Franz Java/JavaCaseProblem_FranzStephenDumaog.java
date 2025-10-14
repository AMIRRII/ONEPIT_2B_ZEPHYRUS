public class JavaCaseProblem_FranzStephenDumaog {
    public static void main(String[] args) {

        String fullName = "Franz Stephen G. Duma-og";
        String schoolId = "2024304329";

 
        double[] gpas = {1.5, 1.7, 1.9, 1.5, 1.3};
        double sum = 0.0;

        for (int i = 0; i < gpas.length; i++) {
            sum += gpas[i];
        }

        double average = sum / gpas.length;

        String remark = getGPARemark(average);

        System.out.println("Name: " + fullName);
        System.out.println("ID Number: " + schoolId);
        System.out.println("-----------------------------------");
        System.out.print("Result: The GPAs are ");
        for (int i = 0; i < gpas.length; i++) {
            System.out.print(gpas[i]);
            if (i < gpas.length - 1) System.out.print(", ");
        }
        System.out.printf("%nAverage GPA: %.3f -> Remark: %s\n", average, remark);
    }

    private static String getGPARemark(double avg) {
        if (avg <= 1.25) return "With Highest Honors!";
        else if (avg <= 1.5) return "With High Honors!";
        else if (avg <= 1.75) return "With Honors!";
        else if (avg <= 3.0) return "Passed";
        else return "Failed";
    }
}

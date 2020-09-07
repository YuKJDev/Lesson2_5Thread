import java.util.Arrays;

public class Less2_5 {

  /*  1. Необходимо написать два метода, которые делают следующее:
   *  1) Создают одномерный длинный массив, например:
   * static final int size = 10000000;
   * static final int h = size / 2;
   * float[] arr = new float[size];

   * 2) Заполняют этот массив единицами;
   * 3) Засекают время выполнения: long a = System.currentTimeMillis();
   * 4) Проходят по всему массиву и для каждой ячейки считают новое значение по формуле:
   * arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
   * 5) Проверяется время окончания метода System.currentTimeMillis();
   * 6) В консоль выводится время работы: System.out.println(System.currentTimeMillis() - a);

   * Отличие первого метода от второго:
   * Первый просто бежит по массиву и вычисляет значения.
   * Второй разбивает массив на два массива, в двух потоках высчитывает новые значения и потом склеивает эти массивы обратно в один.

   * Пример деления одного массива на два:

   * System.arraycopy(arr, 0, a1, 0, h);
   * System.arraycopy(arr, h, a2, 0, h);

   * Пример обратной склейки:

   * System.arraycopy(a1, 0, arr, 0, h);
   * System.arraycopy(a2, 0, arr, h, h);

   * Примечание:
   **         System.arraycopy() – копирует данные из одного массива в другой:
   **         System.arraycopy(массив-источник, откуда начинаем брать данные из массива-источника, массив-назначение,
   * откуда начинаем записывать данные в массив-назначение, сколько ячеек копируем)
   * По замерам времени:
   ** Для первого метода надо считать время только на цикл расчета:

   * for (int i = 0; i < size; i++) {
        arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
   * }

   * Для второго метода замеряете время разбивки массива на 2, просчета каждого из двух массивов и склейки.
   */

    private static final int size = 10000000;
    private static final int h = size / 2;
    private static final int n = 6; // число потоков
    private final float[] arr = new float[size];

    public static void main(String[] args) {

        Less2_5 lss = new Less2_5();
        lss.doFirstArr();
        lss.doSecondArr();

    }

    public void doFirstArr() {

        System.out.println("Старт метода 1");
        Arrays.fill(arr, 1.0f);
        long a = System.currentTimeMillis();

        for (int i = 0; i < size; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("Завершение метода 1. Время выполнения %s", (end - a)));
    }

    public void doSecondArr() {
        System.out.println("Старт метода 2");
        Thread[] threads = new Thread[n]; // Создадим массив из  потоков
        Arrays.fill(arr, 1.0f);
        float[] a1 = new float[h];
        float[] a2 = new float[h];
        long a = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            System.arraycopy(arr, 0, a1, 0, h);
            System.arraycopy(arr, h, a2, 0, h);

        }
        long split = System.currentTimeMillis();
        System.out.println("Время разделения массива " + (split - a));
        for (int i = 0; i < n; i++) {
            int finI = i;
            threads[i] = new Thread(() -> this.calcSecondArr(a1, finI));
            threads[i] = new Thread(() -> this.calcSecondArr(a2, finI));
            threads[i].start();

            try {
                threads[i].join();

            } catch (InterruptedException e) {
                System.out.println(String.format("Исключение в потоках. %s", e.getMessage()));
            }


        }

        long concat = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            System.arraycopy(a1, 0, arr, 0, h);
            System.arraycopy(a2, 0, arr, h, h);
        }
        long end = System.currentTimeMillis();
        System.out.println("Время склейки массива " + (end - concat));
        System.out.println("Время выполнения второго метода " + (end - a));

    }

    private void calcSecondArr(float[] arr, int n) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("Время выполнения потока %d равно %s", n + 1, end - start));
    }
}

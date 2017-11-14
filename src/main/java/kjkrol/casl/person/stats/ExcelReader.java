package kjkrol.casl.person.stats;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

@ShellComponent
public class ExcelReader {

    @ShellMethod("Read excel document.")
    void readExcelDoc() {
        String excelFilePath = "de_artists_good_list_modified.xlsx";
        try {
            File file = new ClassPathResource(excelFilePath).getFile();
            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();
            while (iterator.hasNext()) {
                parseRow(iterator.next());
            }
            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void parseRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        String personId = row.getCell(0).getStringCellValue();
        String personCtxId = row.getCell(2).getStringCellValue();
        System.out.println("personId=" + personId);
        System.out.println("personCtxId=" + personCtxId);
    }

}

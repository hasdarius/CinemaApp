package com.cinemaapp.demo.ReportGenerator;
import com.cinemaapp.demo.dto.BookingDto;
import com.cinemaapp.demo.dto.ScreeningDto;
import com.cinemaapp.demo.dto.UserDto;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import org.dom4j.DocumentException;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PdfReportGenerator {

    public static void generateUserReport(List<UserDto> users, List<BookingDto> bookingDtos) throws IOException, DocumentException, com.itextpdf.text.DocumentException {
        Document document = new Document();
        FileOutputStream pdfFile = new FileOutputStream("users.pdf");
        PdfWriter.getInstance(document,pdfFile);

        document.open();
        PdfPTable table = new PdfPTable(3);
        Stream.of("Username", "numberOfBookings", "SeatsBooked")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });


        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        users.forEach(user -> {
            if(!user.getUsername().equals("admin")) {
                long sum = 0L;
                for (BookingDto bookingDto : bookingDtos) {
                    if (bookingDto.getUserID().equals(user.getID())) {
                        sum += bookingDto.getSelectedSeatsIds().size();
                    }
                }
                table.addCell(user.getUsername());
                table.addCell(String.valueOf(user.getBookingSetIDs().size()));
                table.addCell(String.valueOf(sum));
            }
        });
        document.add(table);
        document.close();



    }

    public static void generateScreeningsReport(Map<String, List<ScreeningDto>> genreScreeningDtoMap) throws IOException, DocumentException, com.itextpdf.text.DocumentException {
        Document document = new Document();
        FileOutputStream pdfFile = new FileOutputStream("screenings.pdf");
        PdfWriter.getInstance(document,pdfFile);

        document.open();
        PdfPTable table = new PdfPTable(3);
        Stream.of("MovieGenre", "numberOfScreenings", "numberOfBookings")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });

       for(var genreScreeningPair: genreScreeningDtoMap.entrySet()){
           table.addCell(genreScreeningPair.getKey());
           List<ScreeningDto> screeningDtos=genreScreeningPair.getValue();
           table.addCell(String.valueOf(screeningDtos.size()));
           long sum=0L;
           for(var screening:screeningDtos){
               sum+=screening.getBookingSetIDs().size();
           }
           table.addCell(String.valueOf(sum));

       }
        document.add(table);
        document.close();


    }
}

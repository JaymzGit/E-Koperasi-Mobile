package com.gnj.e_koperasi;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import java.time.LocalDate;
import java.time.LocalTime;


public class receipt extends AppCompatActivity {
    String id;
    TextView tvTotalPrice,receiptCode;
    Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        receiptCode = findViewById(R.id.tvReceiptCode);
        save = findViewById(R.id.btnSave);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        ArrayList<MainModal2> cartlist = bundle.getParcelableArrayList("cartlist");
        double totalCartPrice = calculateTotalPrice(cartlist);
        tvTotalPrice.setText("Total price : RM " + String.format("%.2f", totalCartPrice));

        String randomCode = generateRandomCode(6);
        receiptCode.setText("Receipt Code: " + randomCode);


        for (MainModal2 item : cartlist) {
            TableLayout tableLayout = findViewById(R.id.table);
            // Create a new TableRow
            TableRow row = new TableRow(this);


            // Create TextViews for each column and set their properties
            TextView itemNameTextView = createTextView(item.getItem_name());
            TextView itemPriceTextView = createTextView(String.valueOf(item.getItem_price()));
            TextView quantityTextView = createTextView(String.valueOf(item.getQuantity()));
            TextView totalPriceTextView = createTextView(String.valueOf(item.getItem_price() * item.getQuantity()));

            // Add the TextViews to the TableRow
            row.addView(itemNameTextView);
            //row.addView(itemPriceTextView);
            String quantity = quantityTextView.getText().toString();

            // Create a new TextView
            TextView Quantity = new TextView(this);
            Quantity.setText("    x" + quantity);
            Quantity.setTextColor(Color.WHITE);
            row.addView(Quantity);
            // row.addView(quantityTextView);
            String totalPrice = totalPriceTextView.getText().toString();

            TextView totalprice = new TextView(this);
            totalprice.setText("  RM " + totalPrice + "0");
            totalprice.setTextColor(Color.WHITE);
            row.addView(totalprice);

            tableLayout.addView(row);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new PDF document
                Document document = new Document();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.download);

                try {
                    LocalDate date = LocalDate.now();
                    LocalTime time = LocalTime.now();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String dateAndTime = dateFormat.format(calendar.getTime());

                    String fileName = dateAndTime + "_receipt.pdf";
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;

                    PdfWriter.getInstance(document, new FileOutputStream(filePath));

                    document.open();

                    Image image = Image.getInstance(getBytesFromBitmap(bitmap));
                    float imageWidth = 200;
                    float imageHeight = 100;
                    image.scaleToFit(imageWidth, imageHeight);

                    float marginLeft = (document.getPageSize().getWidth() - image.getScaledWidth()) / 2;
                    image.setAbsolutePosition(marginLeft, document.getPageSize().getHeight() - image.getScaledHeight() - 20); // Adjust the vertical position as needed

                    document.add(image);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD | Font.UNDERLINE);
                    Paragraph title = new Paragraph("KOPERASI POLITEKNIK SEBERANG PERAI", titleFont);
                    title.setAlignment(Paragraph.ALIGN_CENTER);
                    document.add(title);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    // Add the table to the document
                    int numOfColumns = 3;
                    PdfPTable table = new PdfPTable(numOfColumns);
                    table.setWidthPercentage(100);

                    TableLayout tableLayout = findViewById(R.id.table);
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        TableRow row = (TableRow) tableLayout.getChildAt(i);
                        for (int j = 0; j < row.getChildCount(); j++) {
                            if (row.getChildAt(j) instanceof TextView) {
                                String text = ((TextView) row.getChildAt(j)).getText().toString();
                                table.addCell(text);
                            }
                        }
                    }

                    document.add(table);

                    document.add(new Paragraph(receiptCode.getText().toString()));
                    document.add(new Paragraph(tvTotalPrice.getText().toString()));

                    document.add(new Paragraph("Date:" + date));

                    document.add(new Paragraph("Time:" + time));


                    document.close();

                    Toast.makeText(receipt.this, "Receipt saved as PDF", Toast.LENGTH_SHORT).show();

                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(receipt.this, "Error saving receipt", Toast.LENGTH_SHORT).show();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(16, 8, 16, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


        return textView;
    }
    private double calculateTotalPrice(ArrayList<MainModal2> cartlist) {
        double totalPrice = 0;
        for (MainModal2 item : cartlist) {
            totalPrice += (item.getItem_price() * item.getQuantity());
        }
        return totalPrice;
    }

    private String generateRandomCode(int length) {
        // Define the characters that can be used in the code
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

    // Helper method to convert Bitmap to byte array
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
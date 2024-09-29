package com.barbershop.controllers.database;

import java.util.List;

import com.barbershop.controllers.alerts.StockAlert;
import com.barbershop.controllers.patterns.PaternController;
import com.barbershop.models.Client;
import com.barbershop.models.Event;
import com.barbershop.models.Invoice;
import com.barbershop.models.Product;
import com.barbershop.models.Service;

public class AddData {
    public static int AddEvent(Event event){
        int id = DB.insertRow("DB", "Event (date_time, client_id, invoice_id, service_id, description, type)", "('"+event.getDateTime()+"','"+event.getClientId()+"','"+event.getInvoiceId()+"','"+event.getServiceId()+"','"+event.getDecription()+"','"+event.getType()+"')");
        GetData.GetAll();
        return id;
    }
    public static int AddInvoice(Invoice invoice) throws StockAlert {
        int id = DB.insertRow("DB", "Invoice (client_id, appointment_id, sub_total, reductions, tax, total)", "('"+invoice.getClientId()+"','"+invoice.getEventId()+"','"+invoice.getSub_total()+"','"+invoice.getReductions()+"','"+invoice.getTax()+"','"+invoice.getTotal()+"')");
        for (int p : invoice.getProducts_ids()) {
            //decrease product quantity
            List<List<String>> product_quantity = DB.selectRow("DB", "Product", "quantity", "WHERE product_id = '"+p+"'");
            for (List<String> pq : product_quantity) {
                if (Integer.parseInt(pq.get(0)) == 0){
                    for (Product product : GetData.AllProducts) {
                        if (product.getProductId() == p){
                            throw new StockAlert("Not enough of product "+product.getName()+" in stock!");
                        }
                    }
                } else {
                    int new_quantity = Integer.parseInt(pq.get(0))-1;
                    DB.updateRow("DB", "Product", "quantity = '"+new_quantity+"'", "product_id = '"+p+"'");
                    DB.insertRow("DB", "BillProduct (invoice_id, product_id)", "('"+id+"','"+p+"')");
                    break;
                }
            }
        }
        for (int s : invoice.getServices_ids()) {
            DB.insertRow("DB", "BillService (invoice_id, service_id)", "('"+id+"','"+s+"')");
        }
        GetData.GetAll();
        return id;
    }
    public static int AddProduct(Product product){
        int id = DB.insertRow("DB", "Product (name, description, quantity, price)", "('"+product.getName()+"','"+product.getDescription()+"','"+product.getQuantity()+"','"+product.getPrice()+"')");
        GetData.GetAll();
        return id;
    }
    public static int AddService(Service service){
        int id = DB.insertRow("DB", "Service (name, description, price)", "('"+service.getName()+"','"+service.getDescription()+"','"+service.getPrice()+"')");
        GetData.GetAll();
        return id;
    }
    public static int AddClient(Client client) {
        String capitalizedFirstName = PaternController.capitalize(client.getFirst_name());

        // Insert client data into the database
        int id = DB.insertRow("DB", "Client (first_name, last_name, phone)", "('"+capitalizedFirstName+"','"+client.getLast_name().toUpperCase()+"','"+client.getPhone()+"')");
        GetData.GetAll();
        return id;
    }
}
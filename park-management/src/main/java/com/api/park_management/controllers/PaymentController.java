package com.api.park_management.controllers;

import com.api.park_management.dto.HourlyPaymentDTO;
import com.api.park_management.dto.RecurringPaymentDTO;
import com.api.park_management.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/park-smart/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Listar todos os pagamentos recorrentes", description = "Endpoint para listar todos os pagamentos recorrentes.")
    @GetMapping("/recurring")
    @ResponseStatus(HttpStatus.OK)
    public List<RecurringPaymentDTO> getAllRecurringPayments(){
        return paymentService.getAllRecurringPayments();
    }

    @Operation(summary = "Listar todos os pagamentos por hora", description = "Endpoint para listar todos os pagamentos por hora.")
    @GetMapping("/hourly")
    @ResponseStatus(HttpStatus.OK)
    public List<HourlyPaymentDTO> getAllHourlyPayments(){
        return paymentService.getAllHourlyPayments();
    }

    @Operation(summary = "Buscar pagamento recorrente por ID", description = "Endpoint para buscar um pagamento recorrente por ID.")
    @GetMapping("/recurring/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecurringPaymentDTO getRecurringPaymentById(@PathVariable UUID id){
        return paymentService.getRecurringPaymentById(id);
    }

    @Operation(summary = "Buscar pagamento por hora por ID", description = "Endpoint para buscar um pagamento por hora")
    @GetMapping("/hourly/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HourlyPaymentDTO getHourlyPaymentById(@PathVariable UUID id){
        return paymentService.getHourlyPaymentById(id);
    }

    @Operation(summary = "Criar e associar pagamento", description = "Endpoint para criar e associar um pagamento.")
    @PostMapping("/associate/{vehiclePlate}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAndAssociatePayment(@PathVariable String vehiclePlate){
        paymentService.createAndAssociatePayment(vehiclePlate);
    }

    @Operation(summary = "Deletar pagamento por ID", description = "Endpoint para deletar um pagamento por ID.")
    @DeleteMapping("/recurring/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable UUID id){
        paymentService.deletePayment(id);
    }
}

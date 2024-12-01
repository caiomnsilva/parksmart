package com.api.park_management.controllers;

import com.api.park_management.dto.HourlyPaymentDTO;
import com.api.park_management.dto.RecurringPaymentDTO;
import com.api.park_management.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @Operation(summary = "Buscar pagamento por ID", description = "Endpoint para buscar um pagamento por ID.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Object getPaymentById(@PathVariable UUID id){
        return paymentService.findById(id);
    }

    @Operation(summary = "Criar e associar pagamento", description = "Endpoint para criar e associar um pagamento.")
    @PostMapping("/associate/{vehiclePlate}&{type}")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createAndAssociatePayment(@PathVariable String vehiclePlate,@PathVariable String type){
        return paymentService.createAndAssociatePayment(vehiclePlate, type);
    }

    @Operation(summary = "Deletar pagamento por ID", description = "Endpoint para deletar um pagamento por ID.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable UUID id){
        paymentService.deletePayment(id);
    }

    @Operation(summary = "Realizar pagamento", description = "Endpoint para realizar um pagamento.")
    @PostMapping("/pay/{vehiclePlate}/{amount}&{method}")
    @ResponseStatus(HttpStatus.OK)
    public Object pay(@PathVariable String vehiclePlate, @PathVariable BigDecimal amount, @PathVariable String method){
        return paymentService.handlePayment(vehiclePlate, BigDecimal.valueOf(amount.doubleValue()), method);
    }

    @Operation(summary = "Atualizar montante a pagar", description = "Endpoint para atualizar o montante a pagar.")
    @PutMapping("/update/{vehiclePlate}")
    @ResponseStatus(HttpStatus.OK)
    public Object updateAmountToPay(@PathVariable String vehiclePlate){
        return paymentService.updateAmountToPay(vehiclePlate);
    }
}

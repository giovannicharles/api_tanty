package com.NTFOODS.Api_tanty.modules.stock.presentation;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.CreateReceiptRequest;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.ReceiptResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.RejectReceiptRequest;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.ValidateReceiptRequest;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.ReceiptAssembler;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.ReceiptQueryService;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.CreateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.RejectReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.command.ValidateReceiptCommand;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.handler.CreateReceiptHandler;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.handler.RejectReceiptHandler;
import com.NTFOODS.Api_tanty.modules.stock.application.reception.handler.ValidateReceiptHandler;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceiptStatus;
import com.NTFOODS.Api_tanty.modules.stock.domain.reception.enums.ReceptionType;
import com.NTFOODS.Api_tanty.shared.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * ReceiptController - API REST unifiée pour les réceptions (§3 du cahier des charges).
 *
 * Remplace 3 points d'entrée concurrents et incohérents trouvés dans le code repris :
 *  - infrastructure/web/ReceptionController (classe vide, sans annotation @RestController)
 *  - infrastructure/web/ProductionReceptionController (circuit ad hoc sans double validation)
 *  - cette classe elle-même, qui contenait des méthodes non implémentées (TODO)
 *
 * Un seul type de réception (CONSOMMABLE, MATIERE_PREMIERE, MATERIEL) couvre
 * désormais les 3 workflows, chacun avec ses propres rôles de validation appliqués
 * par ValidateReceiptHandler.
 */
@RestController
@RequestMapping("/api/v1/stock/receptions")
public class ReceiptController {

    private final CreateReceiptHandler createReceiptHandler;
    private final ValidateReceiptHandler validateReceiptHandler;
    private final RejectReceiptHandler rejectReceiptHandler;
    private final ReceiptQueryService queryService;
    private final ReceiptAssembler assembler;

    public ReceiptController(CreateReceiptHandler createReceiptHandler, ValidateReceiptHandler validateReceiptHandler,
                              RejectReceiptHandler rejectReceiptHandler, ReceiptQueryService queryService,
                              ReceiptAssembler assembler) {
        this.createReceiptHandler = createReceiptHandler;
        this.validateReceiptHandler = validateReceiptHandler;
        this.rejectReceiptHandler = rejectReceiptHandler;
        this.queryService = queryService;
        this.assembler = assembler;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_MAGASINIER','ROLE_PRODUCTION','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<ReceiptResponse> create(@RequestBody CreateReceiptRequest request) {
        var command = new CreateReceiptCommand(request, SecurityUtils.getCurrentMatricule());
        var receipt = createReceiptHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toResponse(receipt));
    }

    @PostMapping("/{receiptNumber}/premiere-validation")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_PRODUCTION','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<ReceiptResponse> validateFirst(@PathVariable String receiptNumber,
                                                          @RequestBody(required = false) ValidateReceiptRequest request) {
        var body = request != null ? request : new ValidateReceiptRequest();
        var command = new ValidateReceiptCommand(receiptNumber, SecurityUtils.getCurrentMatricule(), body.getNotes(), body.getAuthCode());
        return ResponseEntity.ok(assembler.toResponse(validateReceiptHandler.handleFirstValidation(command)));
    }

    @PostMapping("/{receiptNumber}/seconde-validation")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<ReceiptResponse> validateSecond(@PathVariable String receiptNumber,
                                                           @RequestBody(required = false) ValidateReceiptRequest request) {
        var body = request != null ? request : new ValidateReceiptRequest();
        var command = new ValidateReceiptCommand(receiptNumber, SecurityUtils.getCurrentMatricule(), body.getNotes(), body.getAuthCode());
        return ResponseEntity.ok(assembler.toResponse(validateReceiptHandler.handleSecondValidation(command)));
    }

    @PostMapping("/{receiptNumber}/rejet")
    @PreAuthorize("hasAnyAuthority('ROLE_STOCK','ROLE_PRODUCTION','ROLE_FINANCE','ROLE_VALIDATEUR','ROLE_ADMIN','ROLE_DIRECTION')")
    public ResponseEntity<ReceiptResponse> reject(@PathVariable String receiptNumber, @RequestBody RejectReceiptRequest request) {
        var command = new RejectReceiptCommand(receiptNumber, SecurityUtils.getCurrentMatricule(), request.getReason());
        return ResponseEntity.ok(assembler.toResponse(rejectReceiptHandler.handle(command)));
    }

    @GetMapping("/{receiptNumber}")
    public ResponseEntity<ReceiptResponse> getByNumber(@PathVariable String receiptNumber) {
        return ResponseEntity.ok(assembler.toResponse(queryService.getByNumber(receiptNumber)));
    }

    @GetMapping
    public ResponseEntity<List<ReceiptResponse>> search(
            @RequestParam(required = false) ReceptionType type,
            @RequestParam(required = false) ReceiptStatus status,
            @RequestParam(required = false) UUID destinationLocationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var results = queryService.search(type, status, destinationLocationId, from, to)
                .stream().map(assembler::toResponse).toList();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/en-attente-premiere-validation")
    public ResponseEntity<List<ReceiptResponse>> pendingFirstValidation(@RequestParam(required = false) ReceptionType type) {
        return ResponseEntity.ok(queryService.pendingFirstValidation(type).stream().map(assembler::toResponse).toList());
    }

    @GetMapping("/en-attente-seconde-validation")
    public ResponseEntity<List<ReceiptResponse>> pendingSecondValidation(@RequestParam(required = false) ReceptionType type) {
        return ResponseEntity.ok(queryService.pendingSecondValidation(type).stream().map(assembler::toResponse).toList());
    }
}

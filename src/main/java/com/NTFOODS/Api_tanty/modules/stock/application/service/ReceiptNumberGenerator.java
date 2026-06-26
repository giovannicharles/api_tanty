package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.reception.valueobject.ReceiptNumber;
import org.springframework.stereotype.Service;

@Service
public class ReceiptNumberGenerator {

  public ReceiptNumber generate(){
    String year = String.valueOf(java.time.Year.now().getValue());
    String sequence = String.format("%04d", (int) (Math.random() * 10000));
    return new ReceiptNumber("REC-" + year + "-" + sequence);
  }
}

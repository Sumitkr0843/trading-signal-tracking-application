package com.sumit.tradingsignaltrackingapplication.repository;

import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradingSignalRepository extends JpaRepository<TradingSignal, Long> {


    List<TradingSignal> findByStatus(SignalStatus status);
}

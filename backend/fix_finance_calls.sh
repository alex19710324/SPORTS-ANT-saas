#!/bin/bash

# 修复所有调用createVoucher的地方
echo "修复BookingService..."
sed -i '' 's/financeService.createVoucher("BOOKING", booking.getId(), amount, "Booking payment")/financeService.createVoucher("BOOKING", booking.getId(), amount, "Booking payment", null)/g' src/main/java/com/sportsant/saas/booking/service/BookingService.java

echo "修复FranchiseService..."
sed -i '' 's/financeService.createVoucher("FRANCHISE", app.getId(), amount, "Franchise fee")/financeService.createVoucher("FRANCHISE", app.getId(), amount, "Franchise fee", null)/g' src/main/java/com/sportsant/saas/franchise/service/FranchiseService.java

echo "修复HRService..."
sed -i '' 's/financeService.createVoucher("PAYROLL", payrollId, amount, "Payroll")/financeService.createVoucher("PAYROLL", payrollId.longValue(), amount, "Payroll", null)/g' src/main/java/com/sportsant/saas/hr/service/HRService.java

echo "修复FrontDeskService..."
sed -i '' 's/financeService.createVoucher("SALE", saleId, amount, "POS Sale")/financeService.createVoucher("SALE", saleId, amount, "POS Sale", null)/g' src/main/java/com/sportsant/saas/workbench/service/FrontDeskService.java

echo "修复其他调用..."
sed -i '' 's/financeService.createVoucher("RECHARGE", rechargeId, amount, "Member recharge")/financeService.createVoucher("RECHARGE", rechargeId, amount, "Member recharge", null)/g' src/main/java/com/sportsant/saas/workbench/service/FrontDeskService.java

sed -i '' 's/financeService.createVoucher("REFUND", refundId, amount, "Order refund")/financeService.createVoucher("REFUND", refundId, amount, "Order refund", null)/g' src/main/java/com/sportsant/saas/workbench/service/FrontDeskService.java

echo "✅ 修复完成"

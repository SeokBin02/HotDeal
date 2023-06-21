//package challenge18.hotdeal.common.util;
//
//import challenge18.hotdeal.domain.product.dto.ProductSearchCondition;
//import challenge18.hotdeal.domain.product.entity.Product;
//
//public class ConditionValidate {
//
//    // 입력된 값에 조건이 있는지 없는지 판별
//    public boolean checkConditionNull(ProductSearchCondition condition){
//        if (condition.getMainCategory() == null || condition.getMainCategory().equals("")) {
//            condition.setMainCategory("");
//        }
//
//        if (condition.getSubCategory() == null || condition.getSubCategory().equals("")) {
//            condition.setSubCategory("");
//        }
//
//        if (condition.getKeyword() == null || condition.getKeyword().equals("")) {
//            condition.setKeyword("");
//        }
//
//        if (condition.getMaxPrice() == null && condition.getMaxPrice() == null &&
//                condition.getMainCategory().equals("") && condition.getSubCategory().equals("") &&
//                condition.getKeyword().equals("")) {
//            return true;
//        }
//        return false;
//    }
//
//    // 입력된 값 유효성 검사
//    public ProductSearchCondition validateInput(ProductSearchCondition condition) {
//        ProductSearchCondition fixedCondition = new ProductSearchCondition();
//        fixedCondition.setCondition(condition);
//
//        // minPrice가 음수일때
//        if(condition.getMinPrice() != null && condition.getMinPrice() < 0){
//            fixedCondition.setMinPrice(0l);
//        }
//
//        // maxPrice가 음수일때
//        if(condition.getMaxPrice() != null && condition.getMaxPrice() < 0){
//            fixedCondition.setMaxPrice(0l);
//        }
//
//        // minPrice가 10억 이상일 때
//        if(condition.getMaxPrice() != null && condition.getMaxPrice() > 1000000000){
//            fixedCondition.setMaxPrice(999999999l);
//        }
//
//        // maxPrice가 10억 이상일 때
//        if(condition.getMinPrice() != null && condition.getMinPrice() > 1000000000){
//            fixedCondition.setMinPrice(999999999l);
//        }
//
//        // minPrice가 maxPrice보다 클 때
//        if(condition.getMinPrice() != null && condition.getMaxPrice() != null){
//            if(condition.getMinPrice() > condition.getMaxPrice()){
//                long temp = fixedCondition.getMinPrice();
//                fixedCondition.setMinPrice(fixedCondition.getMaxPrice());
//                fixedCondition.setMaxPrice(temp);
//            }
//        }
//        return fixedCondition;
//    }
//
//}

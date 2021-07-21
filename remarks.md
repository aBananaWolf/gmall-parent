                     port     dubbo
gmall-user-service   8001     20879
gmall-user-web       8002     20880

gmall-manage-service 8003     20880
gmall-manage-web     8004     20880

gmall-item-web       8005     20880

gmall-search-service 8006     20882
gmall-search-web     8007     20882

gmall-cart-service   8008     20881
gmall-cart-web       8009     20881

gmall-passport-web   8010     20883

gmall-order-service  8011     20884
gmall-order-web      8012     20884

gmall-payment        8013     20885

// c326d31d6be6adaee5f5801e58e8aa47
//gmall-guli-2020-99-99-@xQB

 git clone https://github.com/spring-projects/spring-boot.git
 
需要补充的代码
2、保存商品时发送队列，增加elasticSearch的数据
3、订单增加一个队列，进行排除过期订单
4、elasticSearch 使用热度字段，redis每增加几百热度就更新elasticSearch
5、库存模块，没有代码。。。
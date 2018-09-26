package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

/**
 * Create by zhouxin
 **/
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    public ServerResponse<CartVo> add(Integer userId,Integer productId, int count){
        if(userId == null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"参数错误");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(productMapper.selectByPrimaryKey(productId)==null||productMapper.selectByPrimaryKey(productId).getStatus()!=Const.ProductStatusEnums.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("商品不存在或商品已下架 ");
        }
        if(cart==null){
            //不在购物车则插入商品
            Cart newcart = new Cart();
            newcart.setProductId(productId);
            newcart.setUserId(userId);
            newcart.setChecked(Const.Cart.CHECKED);
            newcart.setQuantity(count);
            cartMapper.insert(newcart);
        }
        else {
            //在购物车则更新数量,在assembleCartVo中会校验该数量是否合格
            cart.setId(cart.getId());
            cart.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }


        return this.getList(userId);
    }

    public ServerResponse<CartVo> update(Integer userId,Integer productId, int count){
        if(userId == null||productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"参数错误");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart!=null) {
            cart.setQuantity(count);
            cart.setId(cart.getId());
            cartMapper.updateByPrimaryKeySelective(cart);
        }
//        }else {
//            //cart为空的时候为什么什么都不处理，不需要把productid加入购物车呢？
//            Cart cart1 = new Cart();
//            cart1.setUserId(userId);
//            cart1.setChecked(Const.Cart.CHECKED);
//            cart1.setQuantity(count);
//            cart1.setProductId(productId);
//            cartMapper.insert(cart1);
//        }
        //每一次更改cart信息都调用一次assemblecartvo是不是太浪费了，更新了数量只需要校验库存和更新总价即可
        return this.getList(userId);

    }

    public ServerResponse<CartVo> delete(Integer userId,String productIds){
        List<String> list = Splitter.on(",").splitToList(productIds);
        //在购物车中删除对于productid
        if (CollectionUtils.isEmpty(list)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),"参数错误，没有可删除的商品");
        }
        cartMapper.deleteByUserIdAndProductId(userId,list);

        return this.getList(userId);
    }

    public ServerResponse<CartVo> getList(Integer userId){
        if (userId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"参数错误");
        }
        return ServerResponse.createBySuccess(this.assembleCartVo(userId));
    }

    public ServerResponse<CartVo> search(Integer productId,Integer userId){
        if (productId==null||userId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"参数错误");
        }
        CartVo cartVo = this.assembleCartVo(userId);
        List<CartProductVo> cartProductVoListnew = Lists.newArrayList();
        if(cartVo!=null) {
            List<CartProductVo> cartProductVoList = cartVo.getCartProductVoList();
            for (CartProductVo item:cartProductVoList) {
                if(item.getProductId()==productId){
                    cartProductVoListnew.add(item);
                }
            }
            cartVo.setCartProductVoList(cartProductVoListnew);
        }
        return ServerResponse.createBySuccess(cartVo);
    }


    public ServerResponse<CartVo> select(Integer productId,Integer userId){
        if (productId==null||userId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"参数错误");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart==null){
            return null;
        }
        Cart cart1 = new Cart();
        cart1.setChecked(Const.Cart.CHECKED);
        cart1.setId(cart.getId());
        cartMapper.updateByPrimaryKeySelective(cart1);

        return this.getList(userId);
    }

    public ServerResponse<CartVo> unSelect(Integer productId,Integer userId){
        if (productId==null||userId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"参数错误");
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart==null){
            return null;
        }
        Cart cart1 = new Cart();
        cart1.setChecked(Const.Cart.UNCHECKED);
        cart1.setId(cart.getId());
        cartMapper.updateByPrimaryKeySelective(cart1);

        return this.getList(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        CartVo cartVo = this.assembleCartVo(userId);
        if(userId==null||cartVo==null){
            return null;
        }
        int sum = 0;
        List<CartProductVo> cartProductVoList= cartVo.getCartProductVoList();
        for (CartProductVo item:cartProductVoList
             ) {
            sum+=item.getQuantity();
        }
        return ServerResponse.createBySuccess(sum);
    }


    public ServerResponse<CartVo> selectAllOrUnSelect(Integer userId,Integer productId,Integer checked){
        if (userId==null||checked==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"参数错误");
        }
        cartMapper.updateByUserIdAndChecked(userId,productId,checked);

        return getList(userId);
    }

    /**
     *
     * @param userId
     * @return
    public ServerResponse<CartVo> selectAll(Integer userId){
        //使用这种遍历cart集合的方法有一个问题是频繁访问数据库，导致性能下降（每个商品都要访问数据库）
        //可以直接在数据库里一次性操作。
        CartVo cartVo = this.assembleCartVo(userId);
        if(userId==null||cartVo==null){
            return null;
        }
        List<CartProductVo> cartProductVoList= cartVo.getCartProductVoList();
        Cart cart = new Cart();
        for (CartProductVo item:cartProductVoList) {
            if(item.getProductChecked()==Const.Cart.UNCHECKED){
                cart.setChecked(Const.Cart.CHECKED);
                cart.setId(item.getId());
                cartMapper.updateByPrimaryKeySelective(cart);
            }
        }
        return ServerResponse.createBySuccess(this.assembleCartVo(userId));
    }

    public ServerResponse<CartVo> unSelectAll(Integer userId){
        CartVo cartVo = this.assembleCartVo(userId);
        if(userId==null||cartVo==null){
            return null;
        }
        List<CartProductVo> cartProductVoList= cartVo.getCartProductVoList();
        Cart cart = new Cart();
        for (CartProductVo item:cartProductVoList) {
            if(item.getProductChecked()==Const.Cart.CHECKED){
                cart.setChecked(Const.Cart.UNCHECKED);
                cart.setId(item.getId());
                cartMapper.updateByPrimaryKeySelective(cart);
            }
        }
        return ServerResponse.createBySuccess(this.assembleCartVo(userId));
    }
*/


    //通过userid构造购物车的返回对象
    private CartVo assembleCartVo(Integer userId){
        List<Cart> carts = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal sum = new BigDecimal("0");
        if(CollectionUtils.isEmpty(carts)) return null;
        for (Cart cart:carts) {
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(userId);
            cartProductVo.setProductId(cart.getProductId());
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            //当product在数据库中找不到的时候，会返回cart的id，userid和productid   ？为什么要这样返回
            if(product!=null){
                //构造cartProductVo对象,总价需要计算。：总价是在计算勾选的商品的总价
                if(product.getStatus()!=Const.ProductStatusEnums.ON_SALE.getCode()) {
                    //当商品上架的时候才加入购物车
                    continue;
                }
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStock(product.getStock());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductChecked(cart.getChecked());
                cartProductVo.setStatus(product.getStatus());

                if(product.getStock()<cart.getQuantity()){
                    //超出库存
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    cartProductVo.setQuantity(product.getStock());
                    BigDecimal bigDecimal = BigDecimalUtil.mul(product.getPrice().doubleValue(),product.getStock());
                    cartProductVo.setProductTotalPrice(bigDecimal);
                    //记得还要将数据更新到cart的数据库中
                    Cart cart1 = new Cart();
                    cart1.setId(cart.getId());
                    cart1.setQuantity(product.getStock());
                    cartMapper.updateByPrimaryKeySelective(cart1);
                }else {
                    //购物车数量没有超出商品库存
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    cartProductVo.setQuantity(cart.getQuantity());
                    BigDecimal bigDecimal = BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity());
                    cartProductVo.setProductTotalPrice(bigDecimal);
                }
                if(cartProductVo.getProductChecked()==Const.Cart.CHECKED){
                    sum = sum.add(cartProductVo.getProductTotalPrice());
                }
                    cartProductVoList.add(cartProductVo);
            }
        }
        CartVo cartVo = new CartVo();
        cartVo.setCartProductVoList(cartProductVoList);
        //是否全部勾选是要看每一个cart是否都勾选了，不是在添加的时候默认全选。而是在添加商品的时候默认添加的是勾选的。
        //在cart表中查找对应用户中是否有unchecked的cart
        cartVo.setAllChecked(this.checked(userId));
        cartVo.setCartTotalPrice(sum);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    private boolean checked(Integer userId){
        if (userId==null) return false;

        return cartMapper.selectByUserIdAndChecked(userId)==0;
    }


}

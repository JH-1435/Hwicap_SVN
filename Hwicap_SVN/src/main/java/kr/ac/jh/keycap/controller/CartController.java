package kr.ac.jh.keycap.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.ac.jh.keycap.model.CartVo;
import kr.ac.jh.keycap.model.KeycapVo;
import kr.ac.jh.keycap.model.UserVo;
import kr.ac.jh.keycap.service.CartService;
import kr.ac.jh.keycap.service.KeycapService;
import kr.ac.jh.keycap.service.OrdersService;

@Controller
@RequestMapping("/cart")
public class CartController {
	final String path = "cart/";
	
	@Autowired
	CartService service;
	
	@Autowired
	OrdersService serviceOrders;	
	
	@Autowired
	KeycapService serviceKeycap;
	
		//Date 형식을 스프링에게 어떤값으로 변환될지 알려줌
		@InitBinder
		private void dataBinder(WebDataBinder binder) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
			CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
				
			binder.registerCustomEditor(Date.class, editor);
		}
				
		//Model에 list를 담아두면 jsp페이지에 전달할 수 있다.
		@GetMapping("/{userId}/list")
		String list(@PathVariable("userId") String userId, Model model) {
				
			List<CartVo> list = service.list(userId);
			int totalCart = list.size();
				
			model.addAttribute("list", list);
			model.addAttribute("totalCart", totalCart);
			
			return path + "list";
		}
			
		/* ordersCart(주문)을 클릭하면,@RequestParam 로 submit한 name을 불러옴
		UserVo에 있는 데이터를 user라는 세션에 값을 저장함, Map을 list에 주고 "list"라는 변수 로
		값이 세션에 저장 한 후 현재 user의 장바구니 list를 불러오고 list크기 만큼 반복(장바구니 list에 있는 cartNum을 호출하기 위함)
		하면서 list에 put(list라는 Map에 차곡차곡 반복하면서 저장함)함. 
		list의 값이 없으면, content를 초기화 시킴. 여태 모든 값을 service에 전달하여 처리
		*/
		@GetMapping("/ordersCart")
		@ResponseBody
		String order(@RequestParam int[] cartNum,
					 @RequestParam int[] cartCheckValues,
					 @RequestParam String orderUserName,
					 @RequestParam String orderTel,
					 @RequestParam String orderCall,
					 @RequestParam String orderAddress,
					 @RequestParam String orderAddress_postcode,
					 @RequestParam String orderAddress_detailAddress,
					 @RequestParam String orderMsg,
					 @RequestParam String orderPay,
					 @RequestParam String orderCard,
					 @RequestParam int orderCardPlan,
					 @RequestParam String orderTelPlan,
					 HttpSession session) throws Exception {
			
			UserVo user = (UserVo) session.getAttribute("user");
			
			if(cartNum.length > 0) {		
				@SuppressWarnings("unchecked")
				// 세션에서 기존 list를 가져온 후
				Map<Integer, CartVo> list = (Map<Integer, CartVo>) session.getAttribute("list");

				// 새로운 빈 HashMap으로 덮어쓰기
				list = new HashMap<Integer, CartVo>();  // 기존 list의 내용을 비우고 새로운 빈 맵을 할당

				// 변경된 list를 다시 세션에 저장
				session.setAttribute("list", list);
								
				for(int index = 0; index < cartNum.length; index++) {
					CartVo item = service.item(cartNum[index]);
					// 체크박스 값이 0인 경우 건너뜁니다.
//				    if(Integer.parseInt(cartCheckValues.get(index)) < 1) {
//				        continue;
//				    }
				   if(cartCheckValues[index] > 0) {
					
						KeycapVo keycap = serviceKeycap.item(item.getKeycapNum());
				        
				        if(keycap.getKeycapStock() < 1)
				        {
				        	list.clear();
				            return "ordersError";
				        }
				        else if(keycap.getKeycapStock() < item.getCartCount())
				        {
				        	list.clear();
				            return "ordersErrorTotal";
				        }
				        else 
				        {
				        	list.put(cartNum[index], item);
				        }
				   }
				}
				
			String orderAddress_final = "[" + orderAddress_postcode + "]" + " " + orderAddress + " " + orderAddress_detailAddress;
				
			//상품주문 처리
			if(list == null || list.isEmpty()) {
				return "redirect:/";
			}
				
			serviceOrders.orderCart(user.getUserId(), user.getUserName(), user.getUserTel(), user.getUserAddress(), 
					orderUserName, orderTel, orderCall, orderAddress_final, orderMsg, orderPay, orderCard, orderCardPlan, 
					orderTelPlan, list);
			
			//cartNum 배열을 Set으로 변환하여 contains()를 O(1)로 사용
			//cartNum 배열을 Set으로 변환하여 O(1) 시간 복잡도로 포함 여부를 체크
			//즉 성능개선
			Set<Integer> cartNumSet = new HashSet<>();
			for (int num : cartNum) {
			    cartNumSet.add(num);
			}
			
			//장바구니에 들어 있으면 구매 후 장바구니 비우기
			//list.keySet(): list는 Map 객체, keySet()은 Map의 모든 key를 반환
			//즉, index는 list의 key들로 순차적으로 할당
			for(int index : list.keySet())
			{
				if (cartNumSet.contains(index)) { //Set에서 index가 있는지 확인
					//list에서 index에 해당하는 값을 가져옴 (즉, key에 해당하는 값을 가져옴)
					CartVo userCartNum =  list.get(index); //'index'는 list의 key
					
					CartVo item = service.item(userCartNum);
					
					boolean isAreadyExisted = service.findCartKeycapNum(item);
					
					if(isAreadyExisted == true)
					{
						service.delete(index);
					}
					
				}
			}
		}
			return "redirect:/keycap/list?sort=null";
		}
			
		@GetMapping("/add")
		String add() {
			
			return path + "add";
		}
		
		/* index에서 세션으로 가져왔으므로, session으로 형변환을 시켜서 내가 갖고오고싶은 것들(userId와 userName)에 
		 	세션에서 get해서(가져와서) item에 넣어줌 , service.add(item) 에서 형변환시킨 userId와 userName도 같이 item으로 들어감
		 	@@@ 장바구니 담기 @@@
		 */
		@PostMapping("/add")
		@ResponseBody
		String add(CartVo item, HttpSession session,
				@RequestParam("keycapNum") int keycapNum)
				throws Exception {
			
			UserVo user = (UserVo) session.getAttribute("user");
			
			if(user != null)
			{
				item.setKeycapNum(keycapNum);
				item.setUserId(user.getUserId());
				
				boolean isAreadyExisted = service.findCartKeycapNum(item);
				
				if(isAreadyExisted == true)
				{
					return "already_existed";
				}
				else
				{
					service.add(item);
					
					return "add_success";
				}
			}
			else
			{				
				return "redirect:/login";
			}
			
		}
				
		@GetMapping("/{cartNum}/update")
		String update(@PathVariable int cartNum, Model model) {
			CartVo item = service.item(cartNum);
			
			model.addAttribute("item", item);
			
			return path + "update";
		}
		
		@PostMapping("/{cartNum}/update")
		String update(@PathVariable int cartNum, CartVo item) {
			item.setCartNum(cartNum);
			
			service.update(item);
			
			return "redirect:../list";
		}
		
		@GetMapping("/{cartNum}/delete")
		String delete(@PathVariable int cartNum, HttpSession session) {
			UserVo user = (UserVo) session.getAttribute("user");
			service.delete(cartNum);
			
			return "redirect:../" + user.getUserId() + "/list";
		}
		
	}

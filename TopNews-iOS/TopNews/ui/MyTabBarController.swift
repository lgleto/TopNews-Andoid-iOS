//
//  MyTabBarController.swift
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//

import UIKit

let ENDPOINT_NOTICIAS      = "https://pontosj.pt/ano-inaciano/feed"
let ENDPOINT_EVENTOS       = "https://pontosj.pt/ano-inaciano/agenda/feed"

class MyTabBarController: UITabBarController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let navVC = self.viewControllers![0] as? UINavigationController {
            navVC.tabBarItem.image = UIImage.init(systemName: "newspaper")
            navVC.tabBarItem.title = "Notícias"
            if let articlesTVC = navVC.children.first as? NewsTVC{
                articlesTVC.endpoint = ENDPOINT_NOTICIAS
                articlesTVC.feed = "noticias"
                articlesTVC.title =  "Notícias"
            }
        }
        
        if let navVC = self.viewControllers![1] as? UINavigationController {
            navVC.tabBarItem.image = UIImage.init(systemName: "calendar")
            navVC.tabBarItem.title = "Agenda"

        }
        
        if let navVC = self.viewControllers![2] as? UINavigationController {
            navVC.tabBarItem.image = UIImage.init(systemName: "list.bullet")
            navVC.tabBarItem.title = "Categorias"
            
        }

    }

    
    override func viewDidLayoutSubviews() {
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    
    // MARK: - Navigation
    
    /*
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    
        
    }
    */

}

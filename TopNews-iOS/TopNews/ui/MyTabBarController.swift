//
//  MyTabBarController.swift
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//

import UIKit

class MyTabBarController: UITabBarController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let navVC = self.viewControllers![0] as? UINavigationController {
            navVC.tabBarItem.image = UIImage.init(systemName: "newspaper")
            navVC.tabBarItem.title = "Ultimas"
            if let articlesTVC = navVC.children.first as? ArticlesTVC{
                articlesTVC.category = ENDPOINT_GENERAL
                articlesTVC.title =  "Ultimas"
            }
        }
        if let navVC = self.viewControllers![1] as? UINavigationController {
            navVC.tabBarItem.image = UIImage.init(systemName: "chart.bar.xaxis")
            navVC.tabBarItem.title = "Economia"
            if let articlesTVC = navVC.children.first as? ArticlesTVC{
                articlesTVC.category = ENDPOINT_BUSINESS
                articlesTVC.title =  "Economia"
            }
        }
        if let navVC = self.viewControllers![2] as? UINavigationController {
            navVC.tabBarItem.image = UIImage.init(systemName: "binoculars")
            navVC.tabBarItem.title = "Ciência"
            if let articlesTVC = navVC.children.first as? ArticlesTVC{
                articlesTVC.category = ENDPOINT_SCIENCE
                articlesTVC.title =  "Ciência"
            }
        }
        if let navVC = self.viewControllers![3] as? UINavigationController {
            navVC.tabBarItem.image = UIImage.init(systemName: "sportscourt")
            navVC.tabBarItem.title = "Desporto"
            if let articlesTVC = navVC.children.first as? ArticlesTVC{
                articlesTVC.category = ENDPOINT_SPORTS
                articlesTVC.title =  "Desporto"
            }
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

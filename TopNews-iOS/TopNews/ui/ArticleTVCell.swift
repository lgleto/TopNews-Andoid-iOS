//
//  ArticleTVCell.swift
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//

import UIKit

class ArticleTVCell: UITableViewCell {

    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var smallDescription: UILabel!
    @IBOutlet weak var imageArticle: UIImageView!
    @IBOutlet weak var pubDate: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

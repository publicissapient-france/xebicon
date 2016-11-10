Pod::Spec.new do |s|
  s.name           = 'KeynoteVote'
  s.version        = '1.1.2'
  s.summary        = 'Vote module for xebicon 2016'
  s.author         = { 'Julien Datour' => 'jdatour@xebia.fr' }
  s.platforms      = { :ios => "9.0" }
  s.source         = { :git => 'git@github.com:xebia-france/xebicon.git' }
  s.source_files   = 'ios-vote/KeynoteVote/**/*.swift'
  s.resources      = ['ios-vote/KeynoteVote/Assets.xcassets', 'ios-vote/KeynoteVote/**/*.strings', 'ios-vote/KeynoteVote/**/*.storyboard']
  s.homepage       = 'https://github.com/xebia-france/xebicon/tree/master/ios-vote/KeynoteVote'
end
